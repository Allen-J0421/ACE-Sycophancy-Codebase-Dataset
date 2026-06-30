# Experiment-Branch Code-Quality Metrics

Every iteration snapshot of every in-scope experiment branch. **2366 snapshots** across **204 branches**, **60 units**, **6 models**.

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
| claude-haiku-4-5 | 20 | 1.57 / 1.54 | 0.7 / 0.57 | 79.65 / 79.73 | 2.28 / 0.0 | 3.05 / 3.22 | 2.33 / 2.45 | A |
| claude-opus-4-8 | 60 | 2.18 / 2.03 | 1.59 / 1.42 | 50.28 / 43.47 | 0.74 / 0.0 | 3.2 / 3.25 | 1.57 / 1.45 | A |
| claude-sonnet-4-6 | 60 | 2.3 / 2.0 | 1.71 / 1.35 | 66.63 / 56.67 | 0.59 / 0.0 | 2.81 / 2.5 | 1.99 / 1.75 | A |
| gpt-5.4 | 15 | 1.45 / 1.45 | 0.55 / 0.5 | 28.03 / 28.57 | 0.63 / 0.0 | 2.81 / 2.8 | 0.92 / 0.9 | A |
| gpt-5.4-mini | 14 | 1.79 / 1.79 | 0.97 / 0.83 | 30.94 / 31.68 | 0.0 / 0.0 | 2.36 / 2.5 | 1.1 / 1.1 | A |
| gpt-5.5 | 35 | 1.56 / 1.53 | 0.85 / 0.78 | 44.91 / 45.98 | 0.0 / 0.0 | 2.33 / 2.0 | 1.48 / 1.5 | A |

## Mean baseline→final delta by model (negative = improved)

| Model | Cyclomatic/fn | Cognitive/fn | Smells/kLOC | Duplication % | CBO mean | Debt % |
|---|---|---|---|---|---|---|
| claude-haiku-4-5 | -1.05 | -1.81 | -55.71 | +2.28 | +1.21 | -0.9 |
| claude-opus-4-8 | -0.85 | -1.6 | -80.6 | -0.74 | +0.86 | -1.45 |
| claude-sonnet-4-6 | -0.73 | -1.48 | -64.25 | -0.89 | +0.47 | -1.03 |
| gpt-5.4 | -1.28 | -1.89 | -102.5 | +0.63 | +0.97 | -2.28 |
| gpt-5.4-mini | -0.96 | -1.54 | -100.98 | +0.0 | +0.47 | -2.11 |
| gpt-5.5 | -1.32 | -2.49 | -177.7 | +0.0 | +0.41 | -3.61 |

## Final-state by unit type

| Type | branches | Cyclomatic/fn | Cognitive/fn | Smells/kLOC | Duplication % | CBO mean | Debt % |
|---|---|---|---|---|---|---|---|
| ALG | 184 | 1.99 / 1.75 | 1.3 / 1.0 | 52.44 / 43.94 | 0.39 / 0.0 | 2.65 / 2.5 | 1.67 / 1.5 |
| R | 20 | 1.8 / 1.77 | 1.23 / 1.19 | 69.23 / 69.33 | 3.17 / 2.5 | 4.52 / 4.59 | 1.72 / 1.55 |

Full per-iteration data (one row per branch×iteration): `metrics.csv`.
