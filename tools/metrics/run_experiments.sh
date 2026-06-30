#!/usr/bin/env bash
# run_experiments.sh — measure the six metrics on EVERY iteration snapshot of every
# in-scope experiment branch. Resumable (tree-SHA cache + done-set), background-friendly.
#
# Usage:
#   run_experiments.sh [--scope alg|r|g|all] [--branch <name>] [--unit <unit>]
#   (default scope = alg + R001..R010 ; G is opt-in via --scope g)
set -uo pipefail
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$HERE/config.sh"; source "$HERE/lib/common.sh"
source "$HERE/lib/snapshot.sh"; source "$HERE/lib/ck.sh"; source "$HERE/lib/sonar.sh"

SCOPE="default"; ONLY_BRANCH=""; ONLY_UNIT=""
while [ $# -gt 0 ]; do case "$1" in
  --scope) SCOPE="$2"; shift 2;;
  --branch) ONLY_BRANCH="$2"; shift 2;;
  --unit) ONLY_UNIT="$2"; shift 2;;
  *) echo "unknown arg: $1" >&2; exit 2;;
esac; done

OUT="$RESULTS/experiments"
CACHE="$OUT/cache"; RAW_CK="$OUT/raw/ck"; RAW_SONAR="$OUT/raw/sonar"
ROWS="$OUT/rows.jsonl"; DONE="$OUT/done.txt"; PROG="$OUT/progress.log"
mkdir -p "$CACHE" "$RAW_CK" "$RAW_SONAR"; touch "$ROWS" "$DONE"

in_scope_unit() { # $1 unit -> 0 if in scope
  local u="$1" t; t=$(unit_type "$u")
  case "$SCOPE" in
    alg) [ "$t" = ALG ];;
    r)   [[ "$u" =~ ^R0(0[1-9]|10)_ ]];;
    g)   [ "$t" = G ];;
    all) [ "$t" != UNK ];;
    default) [ "$t" = ALG ] || [[ "$u" =~ ^R0(0[1-9]|10)_ ]];;
  esac
}

# measure one tree (idempotent): ensure $CACHE/<tsha>.json exists. echo 0 ok / 1 fail
measure_tree() {
  local commit="$1" tsha="$2" sha8="${2:0:12}"
  local cache="$CACHE/$tsha.json"
  [ -s "$cache" ] && return 0
  local snap="$WORK/snap_$sha8"
  extract_commit "$commit" "$snap"
  local ckout="$RAW_CK/$tsha"; mkdir -p "$ckout"
  local nf; nf=$(java_count "$snap")
  if [ "$nf" -gt "$BIG_REPO_FILE_THRESHOLD" ]; then ck_run_robust "$snap" "$ckout"; else ck_run "$snap" "$ckout"; fi
  local sj="$RAW_SONAR/$tsha.json"
  sonar_scan_dir "$sha8" "$snap" "$sj" || true
  python3 "$HERE/aggregate.py" "$ckout" "$sj" "$cache" 2>/dev/null
  rm -rf "$snap"
  [ -s "$cache" ]
}

log "[run] scope=$SCOPE  starting"
sonar_start || { log "[run] sonar failed to start"; exit 1; }
export SONAR_TOKEN; SONAR_TOKEN=$(sonar_ensure_token) || { log "[run] no token"; exit 1; }

# Build branch list
if [ -n "$ONLY_BRANCH" ]; then BRANCHES="$ONLY_BRANCH"; else BRANCHES="$(exp_branches)"; fi

total_b=0; total_s=0; cached=0; failed=0
while read -r b; do
  [ -n "$b" ] || continue
  u=$(unit_of "$b"); [ -n "$u" ] || continue
  [ -n "$ONLY_UNIT" ] && [ "$u" != "$ONLY_UNIT" ] && continue
  in_scope_unit "$u" || continue
  IFS=$'\t' read -r tool model ts <<<"$(parse_branch "$b")"
  utype=$(unit_type "$u")
  total_b=$((total_b+1))
  idx=0
  # commits oldest->newest ; iteration 0 = baseline root
  while read -r csha; do
    [ -n "$csha" ] || continue
    key="$b|$csha"
    if grep -qxF "$key" "$DONE"; then idx=$((idx+1)); continue; fi
    tsha=$(tree_sha "$csha")
    if [ -s "$CACHE/$tsha.json" ]; then cached=$((cached+1)); else
      if measure_tree "$csha" "$tsha"; then :; else failed=$((failed+1)); echo "FAIL $b iter=$idx $csha" >> "$PROG"; fi
    fi
    # emit identity row
    python3 - "$ROWS" "$u" "$utype" "$tool" "$model" "$ts" "$b" "$idx" "$csha" "$tsha" <<'PY'
import json,sys
rows,*v=sys.argv[1:]
keys=["unit","unit_type","tool","model","timestamp","branch","iteration","commit_sha","tree_sha"]
open(rows,"a").write(json.dumps(dict(zip(keys,v)))+"\n")
PY
    echo "$key" >> "$DONE"
    total_s=$((total_s+1)); idx=$((idx+1))
    [ $((total_s % 25)) -eq 0 ] && log "[run] $u $tool/$model snapshots=$total_s cached_hits=$cached failed=$failed"
  done < <(git -C "$REPO_ROOT" rev-list --reverse "$b" 2>/dev/null)
done <<< "$BRANCHES"

log "[run] measured: branches=$total_b snapshots=$total_s cache_hits=$cached failed=$failed"
log "[run] assembling metrics.csv + REPORT.md"
python3 "$HERE/assemble.py" "$ROWS" "$CACHE" "$OUT/metrics.csv"
python3 "$HERE/report.py" experiments "$OUT/metrics.csv" "$OUT/REPORT.md"
log "[run] DONE -> $OUT/metrics.csv"
