# Experiment-Branch Code-Quality Metrics

Every iteration snapshot of every in-scope experiment branch. **3284 snapshots** across **300 branches**, **60 units**, **5 models**.

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

### Core analysis engines

| Component | Exact version | Role | Metrics it produces |
|---|---|---|---|
| CK (mauricioaniche/ck) | 0.7.0 (fat jar, 15 MB, from Maven Central) | Static AST metrics per class/method | Coupling Between Objects (`cbo`); per-method Cyclomatic Complexity (`wmc`) |
| SonarQube Community Build | 25.1.0.102122 | Server-side analysis + metric model | Cyclomatic (`complexity`), Cognitive (`cognitive_complexity`), Code Smells, Duplication density, Maintainability (SQALE rating + debt) |
| SonarScanner CLI | 8.1.0.6389 (Homebrew) | Client that uploads source analysis to the server | — |
| SonarJava analyzer plugin | 8.8.0.37665 (bundled in SonarQube) | The engine inside Sonar that parses Java | (drives all Sonar metrics above) |

Both CK and SonarJava parse Java with the Eclipse JDT compiler (ECJ) — that's why both hit the
same TypeBinding/JDT NullPointerException on certain files, which the pipeline works around
(CK fault-isolation; Sonar per-bin scanning).

### Runtime / platform

| Layer | Detail |
|---|---|
| JDK for SonarQube | OpenJDK 17.0.19 (`openjdk@17`) — required; SonarQube 25.1 crashes on Java 21 (SecurityManager removal) |
| JDK for CK | OpenJDK 21.0.11 (default) — CK runs fine on 21 |
| SonarQube embedded search | Elasticsearch 8.16.1 (bundled; SonarQube starts it internally on :9001) |
| SonarQube database | H2 2.3.232 (embedded; ephemeral projects created + deleted per scan) |
| Orchestration language | Bash (macOS zsh env) + Python 3.9.6 (stdlib only — csv, json, statistics; no third-party packages) |
| Snapshot extraction | git `git archive <commit> \| tar -x` (read-only; never touches the working tree) |
| OS / hardware | macOS (arm64), 8 GB RAM (the constraint that forces bin-packed scans for huge repos) |


## Final-state by model (mean / median ±sd across that model's branches)

| Model | branches | Cyclomatic/fn | Cognitive/fn | Smells/kLOC | Duplication % | CBO mean | Debt % | Maint (mode) |
|---|---|---|---|---|---|---|---|---|
| claude-opus-4-8 | 60 | 2.18 / 2.03 ±0.64 | 1.59 / 1.42 ±0.93 | 50.28 / 43.47 ±27.68 | 0.74 / 0.0 ±1.92 | 3.2 / 3.25 ±1.17 | 1.57 / 1.45 ±0.84 | A |
| claude-sonnet-4-6 | 60 | 2.3 / 2.0 ±0.83 | 1.71 / 1.35 ±1.15 | 66.63 / 56.67 ±41.13 | 0.59 / 0.0 ±1.88 | 2.81 / 2.5 ±1.14 | 1.99 / 1.75 ±1.07 | A |
| gpt-5.4 | 60 | 1.51 / 1.5 ±0.19 | 0.63 / 0.57 ±0.25 | 36.79 / 34.46 ±17.17 | 0.42 / 0.0 ±1.84 | 3.37 / 3.12 ±0.93 | 1.2 / 1.1 ±0.7 | A |
| gpt-5.4-mini | 60 | 1.84 / 1.79 ±0.32 | 1.08 / 1.04 ±0.43 | 38.01 / 35.09 ±19.68 | 0.39 / 0.0 ±1.99 | 2.89 / 2.69 ±0.92 | 1.21 / 1.15 ±0.54 | A |
| gpt-5.5 | 60 | 1.62 / 1.59 ±0.27 | 0.81 / 0.72 ±0.5 | 42.48 / 42.23 ±19.53 | 0.4 / 0.0 ±1.81 | 2.66 / 2.42 ±1.01 | 1.34 / 1.3 ±0.61 | A |

## Mean baseline→final delta by model (mean ±sd; negative = improved)

| Model | Cyclomatic/fn | Cognitive/fn | Smells/kLOC | Duplication % | CBO mean | Debt % |
|---|---|---|---|---|---|---|
| claude-opus-4-8 | -0.85 ±1.4 | -1.6 ±2.27 | -80.6 ±81.65 | -0.74 ±3.69 | +0.86 ±1.05 | -1.45 ±1.48 |
| claude-sonnet-4-6 | -0.73 ±1.13 | -1.48 ±1.91 | -64.25 ±82.24 | -0.89 ±3.74 | +0.47 ±0.75 | -1.03 ±1.55 |
| gpt-5.4 | -1.51 ±1.27 | -2.56 ±2.17 | -94.09 ±82.04 | -1.06 ±4.47 | +1.04 ±1.04 | -1.82 ±1.74 |
| gpt-5.4-mini | -1.19 ±1.25 | -2.11 ±2.11 | -92.87 ±86.52 | -1.09 ±4.01 | +0.55 ±0.75 | -1.81 ±1.66 |
| gpt-5.5 | -1.41 ±1.21 | -2.37 ±2.04 | -88.4 ±79.46 | -1.08 ±3.66 | +0.32 ±0.53 | -1.68 ±1.42 |

## Final-state by unit type

| Type | branches | Cyclomatic/fn | Cognitive/fn | Smells/kLOC | Duplication % | CBO mean | Debt % |
|---|---|---|---|---|---|---|---|
| ALG | 250 | 1.92 / 1.73 ±0.64 | 1.17 / 0.88 ±0.91 | 43.34 / 36.27 ±28.89 | 0.1 / 0.0 ±0.79 | 2.69 / 2.57 ±0.87 | 1.42 / 1.2 ±0.84 |
| R | 50 | 1.74 / 1.73 ±0.2 | 1.11 / 1.08 ±0.28 | 64.35 / 60.49 ±18.92 | 2.53 / 0.75 ±3.66 | 4.48 / 4.47 ±0.6 | 1.67 / 1.5 ±0.72 |

Full per-iteration data (one row per branch×iteration): `metrics.csv`.
