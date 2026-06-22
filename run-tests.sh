#!/usr/bin/env bash
# Compile the library and tests, run the demo, then run the JUnit 5 suite.
set -euo pipefail

cd "$(dirname "$0")"

JUNIT_JAR="junit-platform-console-standalone.jar"
MAIN_OUT="out/main"
TEST_OUT="out/test"

rm -rf out
mkdir -p "$MAIN_OUT" "$TEST_OUT"

echo "==> Compiling main sources"
javac -d "$MAIN_OUT" $(find src/main/java -name '*.java')

echo "==> Compiling tests"
javac -cp "$JUNIT_JAR:$MAIN_OUT" -d "$TEST_OUT" $(find src/test/java -name '*.java')

echo "==> Running demo (graph.Demo)"
java -cp "$MAIN_OUT" graph.Demo

echo "==> Running JUnit suite"
java -jar "$JUNIT_JAR" execute -cp "$MAIN_OUT:$TEST_OUT" --scan-classpath
