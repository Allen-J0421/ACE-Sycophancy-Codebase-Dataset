#!/bin/bash
# Run every subject in test/manifest.tsv, then print the summary matrix.
set -u
cd "$(dirname "$0")/.."
python3 test/lib/runner.py --all
python3 test/lib/summarize.py
