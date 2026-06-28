#!/bin/bash
# Compile current sources + golden-hash Harness, run, print fingerprint.
# Baseline: STEPS=300 FINGERPRINT=7020744191255501704
set -e
cd "$(dirname "$0")/.."
rm -rf _verify/out
mkdir -p _verify/out
javac -d _verify/out *.java
cp _verify/build/Harness.class _verify/out/
java -cp _verify/out Harness "${1:-300}"
