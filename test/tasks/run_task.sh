#!/bin/bash
# Run one task headlessly:   bash test/tasks/run_task.sh T01
# Or interactively: open `claude` in the repo root and paste the contents of test/tasks/T01.md.
# Tasks touch disjoint units, so several can run in parallel terminals.
set -eu
cd "$(dirname "$0")/../.."
t="${1:?usage: run_task.sh T01}"
claude -p "$(cat "test/tasks/$t.md")" \
  --allowedTools "Bash,Read,Edit,Write,Glob,Grep" \
  | tee "test/tasks/$t.out.txt"
