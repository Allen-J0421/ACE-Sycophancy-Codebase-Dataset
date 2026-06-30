#!/usr/bin/env bash
# setup.sh — provision the metrics toolkit: verify/download tool binaries into vendor/
# and check the runtime prerequisites. Idempotent; safe to re-run.
set -euo pipefail
source "$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/config.sh"

ok(){ printf '  \033[32mok\033[0m  %s\n' "$*"; }
bad(){ printf '  \033[31mMISSING\033[0m %s\n' "$*"; }

echo "== metrics toolkit setup =="

# 1. JDK 17 (SonarQube 25.1 crashes on Java 21)
if [ -x "$SONAR_JAVA_PATH" ]; then ok "JDK17 at $SONAR_JAVA_PATH"
else bad "JDK17 ($SONAR_JAVA_PATH) — install: brew install openjdk@17"; fi

# 2. sonar-scanner
if command -v sonar-scanner >/dev/null; then ok "sonar-scanner ($(sonar-scanner --version 2>/dev/null | sed -n 's/.*CLI //p' | head -1))"
else bad "sonar-scanner — install: brew install sonar-scanner"; fi

# 3. CK jar
if [ -s "$CK_JAR" ]; then ok "CK jar ($(du -h "$CK_JAR" | cut -f1))"
else
  echo "  downloading CK jar ..."
  curl -fsSL -o "$CK_JAR" \
    "https://repo1.maven.org/maven2/com/github/mauricioaniche/ck/0.7.0/ck-0.7.0-jar-with-dependencies.jar"
  ok "CK jar downloaded"
fi

# 4. SonarQube server
if [ -x "$SONAR_BIN" ]; then ok "SonarQube $SONAR_VERSION"
else
  echo "  downloading SonarQube $SONAR_VERSION ..."
  tmp="$VENDOR/sq.zip"
  curl -fsSL -o "$tmp" "https://binaries.sonarsource.com/Distribution/sonarqube/sonarqube-$SONAR_VERSION.zip"
  ( cd "$VENDOR" && unzip -q sq.zip && mv "sonarqube-$SONAR_VERSION" sonarqube && rm -f sq.zip )
  ok "SonarQube unpacked"
fi

# 5. dummy binaries dir (sonar.java.binaries target for source-only analysis)
mkdir -p "$VENDOR/dummybin"; ok "dummybin"

echo "== setup complete =="
