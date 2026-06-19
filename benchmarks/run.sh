#!/usr/bin/env bash
#
# Self-contained JMH benchmark runner — no Maven/Gradle required.
#
# It downloads the JMH jars into ./lib (once), compiles the library and the
# benchmark with JMH's annotation processor, and runs org.openjdk.jmh.Main.
# Any arguments are forwarded to JMH, e.g.:
#
#   benchmarks/run.sh                                  # full suite
#   benchmarks/run.sh -p size=10000 -f 1 -wi 3 -i 5    # quick, smaller dataset
#   benchmarks/run.sh getHit -rf json -rff results.json
#
set -euo pipefail

cd "$(dirname "$0")/.."  # repo root

JMH_VERSION="1.37"
JOPT_VERSION="5.0.4"
MATH3_VERSION="3.6.1"
LIB="lib"
BUILD="build/jmh"

mkdir -p "$LIB" "$BUILD"

fetch() {
    local url="https://repo1.maven.org/maven2/$1"
    local dest="$LIB/$2"
    if [ ! -f "$dest" ]; then
        echo "Downloading $2 ..."
        curl -fsSL "$url" -o "$dest"
    fi
}

fetch "org/openjdk/jmh/jmh-core/${JMH_VERSION}/jmh-core-${JMH_VERSION}.jar" "jmh-core-${JMH_VERSION}.jar"
fetch "org/openjdk/jmh/jmh-generator-annprocess/${JMH_VERSION}/jmh-generator-annprocess-${JMH_VERSION}.jar" "jmh-generator-annprocess-${JMH_VERSION}.jar"
fetch "net/sf/jopt-simple/jopt-simple/${JOPT_VERSION}/jopt-simple-${JOPT_VERSION}.jar" "jopt-simple-${JOPT_VERSION}.jar"
fetch "org/apache/commons/commons-math3/${MATH3_VERSION}/commons-math3-${MATH3_VERSION}.jar" "commons-math3-${MATH3_VERSION}.jar"

CP=""
for jar in "$LIB"/*.jar; do
    CP="$CP:$jar"
done
CP="${CP#:}"

echo "Compiling library + benchmark (JMH annotation processing) ..."
rm -rf "$BUILD"
mkdir -p "$BUILD"
javac -cp "$CP" -d "$BUILD" \
    src/main/java/hashmap/*.java \
    src/jmh/java/hashmap/bench/*.java

echo "Running benchmarks ..."
java -cp "$BUILD:$CP" org.openjdk.jmh.Main "$@"
