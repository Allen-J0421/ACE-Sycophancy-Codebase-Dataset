#!/usr/bin/env bash
# common.sh — shared helpers: logging, portable timeout, SonarQube lifecycle/token,
# and experiment-branch <-> unit mapping (reuses tools/expmap.sh conventions).
# Source config.sh before this file.

log() { printf '%s %s\n' "$(date '+%H:%M:%S')" "$*" >&2; }

# --- portable timeout (no GNU `timeout` on macOS) ----------------------------
# run_to <seconds> <cmd...> ; returns the command's rc, or 124 if killed.
run_to() {
  local secs="$1"; shift
  "$@" & local pid=$!
  { sleep "$secs"; kill -9 "$pid" 2>/dev/null; } & local w=$!
  disown "$w" 2>/dev/null   # keep job-control "Terminated" noise out of logs
  wait "$pid"; local rc=$?
  kill "$w" 2>/dev/null
  return $rc
}

# --- SonarQube server --------------------------------------------------------
sonar_status() { curl -s "$SONAR_HOST/api/system/status" 2>/dev/null | sed -n 's/.*"status":"\([A-Z]*\)".*/\1/p'; }

sonar_wait_up() {
  local n; for n in $(seq 1 40); do
    [ "$(sonar_status)" = "UP" ] && { log "[sonar] UP"; return 0; }
    sleep 6
  done
  log "[sonar] FAILED to come up"; return 1
}

sonar_start() {
  if [ "$(sonar_status)" = "UP" ]; then log "[sonar] already UP"; return 0; fi
  mkdir -p "$SONAR_HOME/logs" "$SONAR_HOME/data" "$SONAR_HOME/temp"  # wrapper needs these
  log "[sonar] starting (Java 17: $SONAR_JAVA_PATH) ..."
  SONAR_JAVA_PATH="$SONAR_JAVA_PATH" "$SONAR_BIN" start >/dev/null 2>&1
  sonar_wait_up
}

sonar_stop() { "$SONAR_BIN" stop >/dev/null 2>&1 || true; log "[sonar] stopped"; }

# Ensure admin password is changed and a token exists; writes token to $SONAR_TOKEN_FILE.
sonar_ensure_token() {
  if [ -s "$SONAR_TOKEN_FILE" ]; then
    local t; t=$(cat "$SONAR_TOKEN_FILE")
    # validate token still works
    if curl -s -u "$t:" "$SONAR_HOST/api/authentication/validate" | grep -q '"valid":true'; then
      echo "$t"; return 0
    fi
  fi
  # first-time: change default admin password (ignore failure if already changed)
  curl -s -u admin:admin -X POST "$SONAR_HOST/api/users/change_password" \
    -d "login=admin&previousPassword=admin&password=$SONAR_ADMIN_PASS" >/dev/null 2>&1
  local tok
  tok=$(curl -s -u "admin:$SONAR_ADMIN_PASS" -X POST "$SONAR_HOST/api/user_tokens/generate" \
        -d "name=metrics-$(date +%s)" | sed -n 's/.*"token":"\([^"]*\)".*/\1/p')
  [ -n "$tok" ] || { log "[sonar] could not obtain token"; return 1; }
  printf '%s' "$tok" > "$SONAR_TOKEN_FILE"
  echo "$tok"
}

# --- Experiment branch enumeration / mapping (expmap.sh conventions) ---------
# All unique experiment branch short-names (local + origin, deduped).
exp_branches() {
  { git -C "$REPO_ROOT" for-each-ref --format='%(refname:short)' refs/heads/ refs/remotes/origin/; } \
    | grep -E '(^|/)(claude|codex)-exp/' | sed 's#^origin/##' | sort -u
}

# unit a branch belongs to, via its root commit -> baseline unit branch tip
unit_of() {
  local root
  root=$(git -C "$REPO_ROOT" rev-list --max-parents=0 "$1" 2>/dev/null | tail -1) || true
  [ -n "${root:-}" ] || root=$(git -C "$REPO_ROOT" rev-list --max-parents=0 "origin/$1" 2>/dev/null | tail -1) || true
  [ -n "${root:-}" ] || return 0
  git -C "$REPO_ROOT" for-each-ref --points-at "$root" --format='%(refname:short)' \
      refs/heads/ refs/remotes/origin/ \
    | sed 's#^origin/##' | grep -E '^([RG]?[0-9]{3})_' | sort -u | head -1 || true
}

# unit name -> type
unit_type() {
  case "$1" in
    R[0-9][0-9][0-9]_*) echo R ;;
    G[0-9][0-9][0-9]_*) echo G ;;
    [0-9][0-9][0-9]_*)  echo ALG ;;
    *) echo UNK ;;
  esac
}

# branch short-name -> "tool<TAB>model<TAB>timestamp"
#   claude-exp/20260618T041019Z-claude-opus-4-8-high-Agent -> claude  claude-opus-4-8  20260618T041019Z
parse_branch() {
  local b="$1"
  local tool="${b%%-exp/*}"             # claude | codex
  local rest="${b#*-exp/}"              # 20260618T041019Z-claude-opus-4-8-high-Agent
  local ts="${rest%%-*}"                # 20260618T041019Z
  local mid="${rest#*-}"                # claude-opus-4-8-high-Agent
  local model="${mid%-high-Agent}"      # claude-opus-4-8
  [ "$model" = "$mid" ] && model="${mid%-Agent}"   # fallback for non -high- names
  printf '%s\t%s\t%s' "$tool" "$model" "$ts"
}
