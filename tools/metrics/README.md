# `tools/metrics` — code-quality metrics toolkit (v1)

Computes six code-quality metrics on Java snapshots and aggregates them into CSV + Markdown
reports. Built to measure both the **80 baseline snapshots** and **every iteration of every
experiment branch** in this repo, so the sycophancy study can compare how each model's iterative
refactoring moves the metrics relative to baseline.

> v0 was a one-off pipeline in a sandbox; this is the permanent, resumable, reproducible version.

## Metrics & tools

| Metric | Tool | Field(s) |
|---|---|---|
| Cyclomatic Complexity | SonarQube `complexity` (+ CK method `wmc`) | `cc_total`, `cc_per_fn`, `cc_mean_method_ck`, `cc_max_method_ck` |
| Cognitive Complexity | SonarQube `cognitive_complexity` | `cognitive_total`, `cognitive_per_fn` |
| Code Smell Count | SonarQube `code_smells` | `code_smells`, `smells_per_kloc` |
| Duplication Ratio | SonarQube `duplicated_lines_density` | `duplication_pct` |
| Coupling Between Objects | CK 0.7.0 class-level `cbo` | `cbo_mean`, `cbo_median`, `cbo_max` |
| Maintainability | SonarQube SQALE | `maint_rating` (A–E), `tech_debt_ratio_pct`, `tech_debt_min` |

**Stack:** SonarQube Community Build 25.1 (runs on **Java 17** — crashes on 21) + sonar-scanner 8.1;
CK 0.7.0; Python 3.

## Layout

```
config.sh              paths / versions / heaps / thresholds (edit here)
setup.sh               provision vendor/ (CK jar, SonarQube); verify jdk17 + sonar-scanner
lib/                   common.sh (server, token, timeout, branch→unit), snapshot.sh, ck.sh,
                       sonar.sh, binpack.py, binsum.py
aggregate.py           one snapshot (CK dir + Sonar json) -> metric dict
assemble.py            rows.jsonl + tree-sha cache -> experiments/metrics.csv
assemble_baselines.py  rows.jsonl -> baselines/metrics.csv
report.py              metrics.csv -> REPORT.md  (modes: baselines | experiments)
run_baselines.sh       measure the 80 baseline snapshots (reuses raw if present)
run_experiments.sh     measure every iteration of every in-scope experiment branch
results/
  baselines/    metrics.csv + REPORT.md  (committed); raw/ (gitignored)
  experiments/  metrics.csv + REPORT.md  (committed); cache/, raw/, *.log (gitignored)
vendor/                CK jar + SonarQube (gitignored; from setup.sh)
```

## Usage

```sh
bash tools/metrics/setup.sh                 # one-time: fetch CK + SonarQube into vendor/
bash tools/metrics/run_baselines.sh         # 80 baseline snapshots -> results/baselines/
bash tools/metrics/run_experiments.sh       # default scope = ALG + R001..R010, every iteration
bash tools/metrics/run_experiments.sh --scope g          # opt-in: the 5 large G repos
bash tools/metrics/run_experiments.sh --unit 007_dijkstra_shortest_path   # one unit
bash tools/metrics/run_experiments.sh --branch <exp-branch>               # one branch
```

Scopes: `alg`, `r` (R001–R010), `g`, `all`, or default (`alg` + R001–R010). G is opt-in because
each elasticsearch snapshot is scanned as ~7 bins (~25 min) on an 8 GB box.

## How it works

- **Snapshots** are materialized read-only with `git archive <commit> | tar -x` (never touches the
  working tree or `main`).
- **Tree-SHA cache:** metrics depend only on file content = the git *tree* SHA. Each unique tree is
  measured once (`results/experiments/cache/<tree_sha>.json`) and reused everywhere it appears
  (shared baselines, unchanged iterations). This is both the dedup mechanism and the **resume**
  mechanism — re-running skips cached trees; a `done.txt` skips already-emitted rows.
- **SonarQube** runs as one persistent server; each scan uses an ephemeral project key, polls the
  Compute Engine to completion, fetches measures, then deletes the project to keep the DB small.
- **Huge repos** (G) are bin-packed into ≤5k-Java-file sub-scans and summed (`binpack.py` +
  `binsum.py`): counts add, duplication & debt-ratio are ncloc-weighted, rating re-derived from
  SQALE thresholds. CK uses a fault-isolating recursive runner so one bad file can't abort a repo.
- Every external call is wrapped in a portable watchdog timeout (no GNU `timeout` on macOS).

## Output

`results/experiments/metrics.csv` — **one row per (branch, iteration)** with identity columns
(`unit, unit_type, tool, model, timestamp, branch, iteration, commit_sha, tree_sha`) + the metric
columns. `REPORT.md` summarizes final-state and baseline→final deltas per model and unit type.

## Caveats

1. Sources are analyzed **without compiled bytecode** (`sonar.java.binaries` = empty dir), so a
   minority of bytecode-dependent smell rules are skipped; the AST-based metrics above are unaffected.
2. **Maintainability** is SonarQube's SQALE measure (rating + debt ratio), **not** the classic
   Microsoft Maintainability Index — this toolchain emits no Halstead volume.
3. G repos use the binned approximation described above.
