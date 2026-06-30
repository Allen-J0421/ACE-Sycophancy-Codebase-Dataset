# Experiment-Branch Code-Quality Metrics

Every iteration snapshot of every in-scope experiment branch. **290 snapshots** across **26 branches**, **1 units**, **6 models**.

Baseline values are in `../baselines/REPORT.md`; per-iteration trajectories are in `metrics.csv`.

## Methodology

| Metric | Tool | Definition |
|---|---|---|
| Cyclomatic Complexity | SonarQube `complexity` (+ CK method `wmc`) | McCabe CC; total and per-function |
| Cognitive Complexity | SonarQube `cognitive_complexity` | total and per-function |
| Code Smell Count | SonarQube `code_smells` | rule violations; also per kLOC |
| Duplication Ratio | SonarQube `duplicated_lines_density` | % duplicated lines |
| Coupling Between Objects | CK 0.7.0 class-level `cbo` | mean / median / max across classes |
| Maintainability | SonarQube SQALE | rating A–E + technical-debt ratio |

**Stack:** SonarQube Community Build 25.1 (Java 17) + sonar-scanner 8.1; CK 0.7.0.

**Caveats:** sources analyzed without compiled bytecode (a minority of bytecode-only smell
rules skipped; AST metrics unaffected); Maintainability is SonarQube's SQALE measure, not the
classic Microsoft Maintainability Index; huge repos (G) are scanned in ≤5k-file bins and summed
(counts add; duplication & debt-ratio ncloc-weighted; rating re-derived from SQALE thresholds).


## Final-state by model (mean / median across that model's branches)

| Model | branches | Cyclomatic/fn | Cognitive/fn | Smells/kLOC | Duplication % | CBO mean | Debt % | Maint (mode) |
|---|---|---|---|---|---|---|---|---|
| claude-haiku-4-5 | 1 | 1.22 / 1.22 | 0.41 / 0.41 | 67.61 / 67.61 | 0.0 / 0.0 | 2.65 / 2.65 | 2.2 / 2.2 | A |
| claude-opus-4-8 | 1 | 3.0 / 3.0 | 3.5 / 3.5 | 111.11 / 111.11 | 0.0 / 0.0 | 2.0 / 2.0 | 3.7 / 3.7 | A |
| claude-sonnet-4-6 | 1 | 3.0 / 3.0 | 4.0 / 4.0 | 230.77 / 230.77 | 0.0 / 0.0 | 1.5 / 1.5 | 6.0 / 6.0 | B |
| gpt-5.4 | 1 | 1.31 / 1.31 | 0.31 / 0.31 | 28.57 / 28.57 | 0.0 / 0.0 | 6.0 / 6.0 | 1.0 / 1.0 | A |
| gpt-5.4-mini | 1 | 1.67 / 1.67 | 0.72 / 0.72 | 32.79 / 32.79 | 0.0 / 0.0 | 2.0 / 2.0 | 1.4 / 1.4 | A |
| gpt-5.5 | 21 | 1.55 / 1.57 | 0.88 / 0.92 | 46.72 / 48.78 | 0.0 / 0.0 | 2.48 / 2.5 | 1.55 / 1.6 | A |

## Mean baseline→final delta by model (negative = improved)

| Model | Cyclomatic/fn | Cognitive/fn | Smells/kLOC | Duplication % | CBO mean | Debt % |
|---|---|---|---|---|---|---|
| claude-haiku-4-5 | -1.78 | -3.59 | -224.06 | +0.0 | +0.65 | -4.3 |
| claude-opus-4-8 | +0.0 | -0.5 | -180.56 | +0.0 | +0.0 | -2.8 |
| claude-sonnet-4-6 | +0.0 | +0.0 | -60.9 | +0.0 | -0.5 | -0.5 |
| gpt-5.4 | -1.69 | -3.69 | -263.1 | +0.0 | +4.0 | -5.5 |
| gpt-5.4-mini | -1.33 | -3.28 | -258.88 | +0.0 | +0.0 | -5.1 |
| gpt-5.5 | -1.45 | -3.12 | -244.95 | +0.0 | +0.48 | -4.95 |

## Final-state by unit type

| Type | branches | Cyclomatic/fn | Cognitive/fn | Smells/kLOC | Duplication % | CBO mean | Debt % |
|---|---|---|---|---|---|---|---|
| ALG | 26 | 1.65 / 1.57 | 1.05 / 0.85 | 55.84 / 49.39 | 0.0 / 0.0 | 2.55 / 2.25 | 1.8 / 1.6 |

Full per-iteration data (one row per branch×iteration): `metrics.csv`.
