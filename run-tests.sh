#!/usr/bin/env bash
# Compile and run the JUnit 5 test suite for the cycle-detection library.
# No build tool required: this fetches the JUnit Platform Console Standalone jar
# on first use and drives plain javac. Run from the project root: ./run-tests.sh
set -euo pipefail
cd "$(dirname "$0")"

JUNIT_VERSION=1.10.2
JUNIT_JAR="lib/junit-platform-console-standalone-${JUNIT_VERSION}.jar"
JUNIT_URL="https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/${JUNIT_VERSION}/junit-platform-console-standalone-${JUNIT_VERSION}.jar"

if [ ! -f "$JUNIT_JAR" ]; then
	echo "Fetching JUnit ${JUNIT_VERSION}..."
	mkdir -p lib
	curl -sSL -o "$JUNIT_JAR" "$JUNIT_URL"
fi

MAIN_OUT=build/main
TEST_OUT=build/test
rm -rf "$MAIN_OUT" "$TEST_OUT"
mkdir -p "$MAIN_OUT" "$TEST_OUT"

echo "Compiling library sources..."
javac -d "$MAIN_OUT" *.java

echo "Compiling tests..."
javac -d "$TEST_OUT" -cp "$MAIN_OUT:$JUNIT_JAR" $(find test -name '*.java')

echo "Running tests..."
java -jar "$JUNIT_JAR" execute \
	--class-path "$MAIN_OUT:$TEST_OUT" \
	--scan-class-path="$TEST_OUT" \
	--details=tree
