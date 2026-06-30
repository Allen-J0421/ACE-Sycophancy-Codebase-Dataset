#!/usr/bin/env bash
# config.sh — shared configuration for the metrics toolkit (v1).
# Sourced by every script. All paths are absolute and derived from this file's location.

# --- Locations ---------------------------------------------------------------
METRICS_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$METRICS_DIR/../.." && pwd)"
VENDOR="$METRICS_DIR/vendor"
RESULTS="$METRICS_DIR/results"
WORK="$VENDOR/work"                 # scratch for extracted snapshots (gitignored)

CK_JAR="$VENDOR/ck.jar"
SONAR_VERSION="25.1.0.102122"
SONAR_HOME="$VENDOR/sonarqube"
SONAR_BIN="$SONAR_HOME/bin/macosx-universal-64/sonar.sh"

# --- Java / SonarQube --------------------------------------------------------
# SonarQube 25.1 REQUIRES Java 17 (it crashes on Java 21 — SecurityManager removal).
JDK17_HOME="${JDK17_HOME:-/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home}"
export SONAR_JAVA_PATH="$JDK17_HOME/bin/java"

SONAR_HOST="http://localhost:9000"
SONAR_TOKEN_FILE="$METRICS_DIR/.sonar.token"   # gitignored (*.token)
SONAR_ADMIN_PASS="${SONAR_ADMIN_PASS:-Sonar!2026pass}"

# --- Tuning (sized for an 8 GB-RAM machine) ----------------------------------
CK_HEAP="${CK_HEAP:--Xmx6g}"
SONAR_SCANNER_HEAP="${SONAR_SCANNER_HEAP:--Xmx2g}"   # small units; big repos override to 5g
SONAR_SCANNER_HEAP_BIG="${SONAR_SCANNER_HEAP_BIG:--Xmx5g}"
ES_BIN_CAP="${ES_BIN_CAP:-5000}"      # max Java files per Sonar bin for huge repos
BIG_REPO_FILE_THRESHOLD="${BIG_REPO_FILE_THRESHOLD:-9000}"  # >this many .java -> binned scan
SCAN_TIMEOUT="${SCAN_TIMEOUT:-1800}"  # per-scan watchdog seconds (no GNU timeout on macOS)

# Sonar measures pulled for every scan
SONAR_METRICS="complexity,cognitive_complexity,code_smells,duplicated_lines_density,duplicated_lines,duplicated_blocks,sqale_rating,sqale_index,sqale_debt_ratio,ncloc,lines,files,functions,classes,comment_lines_density"

mkdir -p "$WORK" "$RESULTS"
