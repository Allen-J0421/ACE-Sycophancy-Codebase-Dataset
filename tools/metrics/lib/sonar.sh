#!/usr/bin/env bash
# sonar.sh — SonarQube scan helpers. Each scan uses an ephemeral project key, polls the
# Compute Engine to completion, fetches measures as JSON, then deletes the project to keep
# the H2 DB small across thousands of scans. Source config.sh + common.sh first.

SONAR_TOKEN="${SONAR_TOKEN:-}"   # set by caller (sonar_ensure_token)

# _sonar_scan_one <key> <sources_csv> <heap> <outjson> <workdir> -> 0 on success
_sonar_scan_one() {
  local key="$1" srcs="$2" heap="$3" outjson="$4" wd="$5"
  local log="${outjson%.json}.scan.log"
  SONAR_SCANNER_OPTS="$heap" run_to "$SCAN_TIMEOUT" sonar-scanner \
    -Dsonar.host.url="$SONAR_HOST" -Dsonar.token="$SONAR_TOKEN" \
    -Dsonar.projectKey="$key" -Dsonar.projectName="$key" \
    -Dsonar.sources="$srcs" -Dsonar.java.binaries="$VENDOR/dummybin" \
    -Dsonar.sourceEncoding=UTF-8 -Dsonar.scm.disabled=true \
    -Dsonar.working.directory="$wd" >"$log" 2>&1
  grep -q "EXECUTION SUCCESS" "$log" || { echo "[sonar] scan failed: $key" >&2; return 1; }
  local tid; tid=$(sed -n 's/^ceTaskId=//p' "$wd/report-task.txt" 2>/dev/null)
  local n st=""
  for n in $(seq 1 300); do
    st=$(curl -s -u "$SONAR_TOKEN:" "$SONAR_HOST/api/ce/task?id=$tid" | sed -n 's/.*"status":"\([A-Z]*\)".*/\1/p')
    [ "$st" = "SUCCESS" ] && break
    { [ "$st" = "FAILED" ] || [ "$st" = "CANCELED" ]; } && { echo "[sonar] CE $st: $key" >&2; break; }
    sleep 2
  done
  curl -s -u "$SONAR_TOKEN:" "$SONAR_HOST/api/measures/component?component=$key&metricKeys=$SONAR_METRICS" > "$outjson"
  # tidy: drop the project so the server DB stays small
  curl -s -u "$SONAR_TOKEN:" -X POST "$SONAR_HOST/api/projects/delete" -d "project=$key" >/dev/null 2>&1
  grep -q '"metric"' "$outjson"
}

# sonar_scan_dir <sha8> <srcdir> <outjson> : single or binned scan depending on size.
# Always leaves ONE measures JSON at <outjson> (binned scans are summed via binsum.py).
sonar_scan_dir() {
  local sha="$1" src="$2" outjson="$3"
  mkdir -p "$(dirname "$outjson")" "$VENDOR/dummybin"
  local nf; nf=$(java_count "$src")
  if [ "$nf" -le "$BIG_REPO_FILE_THRESHOLD" ]; then
    _sonar_scan_one "m_$sha" "$src" "$SONAR_SCANNER_HEAP" "$outjson" "$WORK/sw_$sha"
    return $?
  fi
  # --- binned (huge repo) ---
  echo "[sonar] binned scan ($nf files): $sha" >&2
  local bins="$WORK/bins_$sha.tsv" parts="$WORK/parts_$sha"
  mkdir -p "$parts"
  python3 "$METRICS_DIR/lib/binpack.py" "$src" "$ES_BIN_CAP" > "$bins"
  local i srcs ok=0 tot=0
  while IFS=$'\t' read -r i srcs; do
    tot=$((tot+1))
    if _sonar_scan_one "m_${sha}_b$i" "$srcs" "$SONAR_SCANNER_HEAP_BIG" "$parts/bin_$i.json" "$WORK/sw_${sha}_b$i"; then
      ok=$((ok+1))
    fi
  done < "$bins"
  [ "$ok" -gt 0 ] || { echo "[sonar] all bins failed: $sha" >&2; return 1; }
  python3 "$METRICS_DIR/lib/binsum.py" "$parts" > "$outjson"
  rm -rf "$parts" "$bins"
  grep -q '"metric"' "$outjson"
}
