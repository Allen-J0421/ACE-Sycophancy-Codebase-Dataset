#!/usr/bin/env bash
# run_baselines.sh — measure the six metrics on the 80 baseline snapshots present on
# `main` (algorithms/*/ and real_exp/{R,G}*/). Reuses existing raw output if present
# (so it doubles as an aggregate-only step after a migration). Resumable.
set -uo pipefail
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$HERE/config.sh"; source "$HERE/lib/common.sh"
source "$HERE/lib/snapshot.sh"; source "$HERE/lib/ck.sh"; source "$HERE/lib/sonar.sh"

OUT="$RESULTS/baselines"; RAW_CK="$OUT/raw/ck"; RAW_SONAR="$OUT/raw/sonar"
ROWS="$OUT/rows.jsonl"; mkdir -p "$RAW_CK" "$RAW_SONAR"; : > "$ROWS"

# build unit list: name<TAB>path<TAB>type
list_units() {
  for d in "$REPO_ROOT"/algorithms/*/; do [ -d "$d" ] && printf '%s\t%s\tALG\n' "$(basename "$d")" "$d"; done
  for d in "$REPO_ROOT"/real_exp/*/; do
    [ -d "$d" ] || continue; n=$(basename "$d")
    case "$n" in R*) printf '%s\t%s\tR\n' "$n" "$d";; G*) printf '%s\t%s\tG\n' "$n" "$d";; esac
  done
}

log "[baselines] starting"
sonar_start || exit 1
export SONAR_TOKEN; SONAR_TOKEN=$(sonar_ensure_token) || exit 1

while IFS=$'\t' read -r name path typ; do
  ckout="$RAW_CK/$name"; sj="$RAW_SONAR/$name.json"
  if [ ! -s "$ckout/class.csv" ]; then
    log "[ck] $name ($typ)"
    if [ "$(java_count "$path")" -gt "$BIG_REPO_FILE_THRESHOLD" ]; then ck_run_robust "$path" "$ckout"; else ck_run "$path" "$ckout"; fi
  fi
  if [ ! -s "$sj" ]; then
    log "[sonar] $name ($typ)"
    sha8="bl_$name"; sonar_scan_dir "$sha8" "$path" "$sj" || true
  fi
  cache="$OUT/cache_$name.json"
  python3 "$HERE/aggregate.py" "$ckout" "$sj" "$cache" 2>/dev/null
  python3 - "$ROWS" "$typ" "$name" "$cache" <<'PY'
import json,sys
rows,typ,name,cache=sys.argv[1:]
m=json.load(open(cache))
m={"unit_type":typ,"unit":name,**m}
open(rows,"a").write(json.dumps(m)+"\n")
PY
  rm -f "$cache"
done < <(list_units)

# assemble CSV (unit_type,unit + metric cols) and report
python3 "$HERE/assemble_baselines.py" "$ROWS" "$OUT/metrics.csv"
python3 "$HERE/report.py" baselines "$OUT/metrics.csv" "$OUT/REPORT.md"
log "[baselines] DONE -> $OUT/metrics.csv"
