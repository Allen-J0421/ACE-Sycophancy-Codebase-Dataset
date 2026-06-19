#!/usr/bin/env bash
# expmap.sh — resolve experiment branches <-> algorithms from the commit graph.
#
# The pipeline branches each experiment from an algorithm's baseline commit, so
# an experiment branch's ROOT commit == that algorithm branch's tip. We use that
# shared commit as the join key — the branch NAME is never needed. This script
# only READS refs; it writes nothing, so it cannot affect the pipeline.
#
# It scans both local branches and their origin/ mirrors, so it works whether you
# have the branches as local heads or just cloned them (remote-tracking refs).
# Output goes to the terminal (stdout) only; redirect with '>' if you want a file.
#
# Usage:
#   tools/expmap.sh map            # full table: algorithm  model  timestamp  branch
#   tools/expmap.sh of <branch>    # the algorithm a given experiment branch belongs to
#   tools/expmap.sh ls <query>     # experiments for an algorithm (e.g. "007" or "dijkstra")
#   tools/expmap.sh algos          # algorithms with experiment counts
#   tools/expmap.sh models         # experiment counts per model
set -euo pipefail

# Search scope: local refs AND their origin/ mirrors. Names are normalized by
# stripping a leading "origin/" so a branch present in both places counts once.
REFSCOPE=(refs/heads/ refs/remotes/origin/)
norm() { sed 's#^origin/##'; }

# algorithm a branch belongs to, via its root commit -> algo branch tip
algo_of() {
  local root
  root=$(git rev-list --max-parents=0 "$1" 2>/dev/null | tail -1) || true
  [ -n "${root:-}" ] || return 0
  git for-each-ref --points-at "$root" --format='%(refname:short)' "${REFSCOPE[@]}" \
    | norm | grep -E '^[0-9]{3}_' | sort -u | head -1 || true
}

# all experiment branch short-names (deduped across local + origin)
exp_branches() {
  git for-each-ref --format='%(refname:short)' "${REFSCOPE[@]}" \
    | norm | grep -E '^(claude|codex)-exp/' | sort -u || true
}

# branch short-name -> "model<TAB>timestamp" (matches the map header / cut -f2=model)
#   claude-exp/20260618T041019Z-claude-opus-4-8-high-Agent -> claude-opus-4-8  20260618T041019Z
parse() {
  local b="${1#*/}"                     # strip "claude-exp/"
  local ts="${b%%-*}"                   # 20260618T041019Z
  local rest="${b#*-}"                  # claude-opus-4-8-high-Agent
  local model="${rest%-high-Agent}"     # claude-opus-4-8
  printf '%s\t%s' "$model" "$ts"
}

# emit one row per experiment: algorithm \t model \t timestamp \t branch
table() {
  local e a tm
  while read -r e; do
    [ -n "$e" ] || continue
    a=$(algo_of "$e")
    tm=$(parse "$e")
    printf '%s\t%s\t%s\n' "${a:-UNMAPPED}" "$tm" "$e"
  done < <(exp_branches)
}

cmd="${1:-map}"
case "$cmd" in
  map)
    { printf 'ALGORITHM\tMODEL\tTIMESTAMP\tBRANCH\n'; table | sort; } | column -t -s$'\t'
    ;;
  of)
    [ $# -ge 2 ] || { echo "usage: expmap.sh of <branch>" >&2; exit 2; }
    algo_of "$2"
    ;;
  ls)
    [ $# -ge 2 ] || { echo "usage: expmap.sh ls <algo query>" >&2; exit 2; }
    table | awk -F'\t' -v q="$2" 'tolower($1) ~ tolower(q)' | sort | column -t -s$'\t'
    ;;
  algos)
    table | cut -f1 | sort | uniq -c | sort -k2
    ;;
  models)
    table | cut -f2 | sort | uniq -c | sort -rn
    ;;
  *)
    echo "unknown command: $cmd" >&2
    grep -E '^#( |$)' "$0" | sed 's/^# \{0,1\}//'
    exit 2
    ;;
esac
