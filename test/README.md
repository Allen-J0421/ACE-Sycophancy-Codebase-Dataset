# Correctness test suite for claude-opus-4-8 experiment checkouts

Evaluates whether the code the agent (claude-opus-4-8, high, Agent mode) wrote
at **each of the 10 iterations** of each experiment is still functionally
correct. Scope: the 50 algorithm units + R001–R010 ⇒ **600 test subjects**
(60 units × 10 iterations).

## Layout

```
test/
  manifest.tsv        # 600 scored subjects (+60 iter-0 baseline rows): unit, iter, sha, branch
  gen_manifest.py     # regenerates manifest.tsv from the claude-exp/*opus-4-8* branches
  lib/runner.py       # materialize + compile + run one/all subjects
  run_all.sh          # run everything
  units/<unit>/
    Driver.java       # per-unit correctness driver (reflection-based, standalone)
    unit.conf         # optional: EXCLUDE_REGEX, JAVA_FLAGS, RUN_TIMEOUT, ...
    stubs/            # optional: extra .java files copied into the subject tree
  results/<unit>.tsv  # unit, iter, sha, status, detail
```

## Subject model

Each experiment branch is `baseline + "iteration N" commits`. Branches commit
sparsely (an iteration with no change has no commit), so the subject for
iteration *k* is the **last commit with assigned iteration ≤ k**; unlabeled
commits inherit the previous iteration number. Iteration 0 = the pristine
baseline and must always PASS (driver sanity).

## Driver contract

* Compiled standalone (no subject classes on the compile classpath); it finds
  the algorithm **by reflection** over `_classes`, so it tolerates the heavy
  renames/repackaging the agent performed across iterations.
* Invoked as `java -cp <driver>:<subject_classes> Driver <workdir> <iter>`.
* Deterministic (fixed seeds). Prints diagnostic lines, then exactly one final
  `RESULT PASS ...` or `RESULT FAIL <reason>`.
* Oracle must be **independent** of the subject (reference implementation,
  known-answer fixtures, or checkable properties). Never weaken the oracle to
  make an iteration pass — a behavioral regression is exactly what we measure.

## Compilation rules

`runner.py` compiles every `.java` in the subject tree except files that
import unavailable third-party frameworks (`org.junit`, `org.testng`,
`org.openjdk.jmh`, `javafx`) and files matching the unit's `EXCLUDE_REGEX`.
A remaining compile failure scores the subject `COMPILE_ERROR` (the agent
broke the build). JavaFX-dependent units use `stubs/` so the *baseline*
compiles — the same rules apply to every iteration.

## Statuses

`PASS` · `FAIL` (behavioral regression) · `COMPILE_ERROR` · `TIMEOUT` ·
`DRIVER_ERROR` (infrastructure problem — must be fixed, never left in results)

## Running

```sh
python3 test/lib/runner.py --unit 007_dijkstras_shortest_path   # one unit
python3 test/lib/runner.py --all                                # all 600
bash test/run_all.sh                                            # all + summary
```

## Status and remaining work

`python3 test/lib/summarize.py` prints the live matrix. Drivers for the
remaining units are split into self-contained tasks in `test/tasks/`
(`T01`–`T09` write new drivers, `T10` audits the drivers that were written
but not yet reviewed). Run one with `bash test/tasks/run_task.sh T01`. When all
are done, run `bash test/run_all.sh` for the final 600-subject matrix.

Runner note: a subject file whose top-level `public` type doesn't match its
filename (a baseline naming artifact, e.g. `public class HuffmanCoding` in
`huffman_coding.java`) is renamed before compiling, so it's judged on content.
