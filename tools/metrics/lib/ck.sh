#!/usr/bin/env bash
# ck.sh — CK (mauricioaniche/ck) runner. Produces class.csv + method.csv.
# Provides Coupling Between Objects (cbo) and per-method Cyclomatic Complexity (wmc).
# Source config.sh + common.sh first.

# ck_run <srcdir> <outdir> : plain CK run. Returns 0 if metrics extracted.
ck_run() {
  local src="$1" out="$2"; mkdir -p "$out"
  run_to "$SCAN_TIMEOUT" java $CK_HEAP -jar "$CK_JAR" "$src" false 0 false "$out/" \
      >"$out/ck.log" 2>&1
  grep -q "Metrics extracted" "$out/ck.log" && [ -s "$out/class.csv" ]
}

# ck_run_robust <srcdir> <outdir> : fault-isolating CK for large repos.
# A single JDT NullPointerException aborts the whole JVM, so on failure we recurse
# into subdirectories and concatenate the CSVs (header taken from a good partial run).
ck_run_robust() {
  local root="$1" out="$2"; mkdir -p "$out"
  local CLASS="$out/class.csv" METHOD="$out/method.csv" FAIL="$out/failed_dirs.txt"
  : > "$CLASS"; : > "$METHOD"; : > "$FAIL"
  local TMP="$out/_tmp" hdr_class="" hdr_method=""

  _ck_try() { # $1 dir -> 0 ok (appended), 1 crash
    rm -rf "$TMP"; mkdir -p "$TMP"
    run_to "$SCAN_TIMEOUT" java $CK_HEAP -jar "$CK_JAR" "$1" false 0 false "$TMP/" >"$TMP/log" 2>&1
    if grep -q "Metrics extracted" "$TMP/log" && [ -s "$TMP/class.csv" ]; then
      [ -z "$hdr_class" ] && { hdr_class=$(head -1 "$TMP/class.csv"); hdr_method=$(head -1 "$TMP/method.csv" 2>/dev/null); }
      tail -n +2 "$TMP/class.csv" >> "$CLASS"
      [ -f "$TMP/method.csv" ] && tail -n +2 "$TMP/method.csv" >> "$METHOD"
      return 0
    fi
    return 1
  }
  _ck_walk() {
    local dir="$1" depth="$2"
    [ "$(java_count "$dir")" -gt 0 ] || return 0
    if _ck_try "$dir"; then return 0; fi
    local had=0 sub
    for sub in "$dir"/*/; do
      [ -d "$sub" ] || continue
      if [ "$(java_count "$sub")" -gt 0 ]; then had=1; _ck_walk "$sub" $((depth+1)); fi
    done
    [ "$had" = 0 ] && echo "$dir" >> "$FAIL"
  }

  _ck_walk "$root" 0
  # prepend headers
  if [ -n "$hdr_class" ]; then
    { echo "$hdr_class"; cat "$CLASS"; } > "$CLASS.h" && mv "$CLASS.h" "$CLASS"
    { echo "$hdr_method"; cat "$METHOD"; } > "$METHOD.h" && mv "$METHOD.h" "$METHOD"
  fi
  rm -rf "$TMP"
  [ -s "$CLASS" ] && [ "$(wc -l < "$CLASS")" -gt 1 ]
}
