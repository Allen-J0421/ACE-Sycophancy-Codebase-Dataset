# Baseline Code-Quality Metrics — 65 Units

Each unit at its **Initial Commit / Baseline** snapshot.

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


## Per-category summary (mean / median ±sd)

| Category | n | Cyclomatic/fn | Cognitive/fn | Smells/kLOC | Duplication % | CBO mean | Debt % |
|---|---|---|---|---|---|---|---|
| ALG | 50 | 3.22 / 3.0 ±1.32 | 3.46 / 3.0 ±2.27 | 138.76 / 106.63 ±84.8 | 0.0 / 0.0 ±0.0 | 1.86 / 2.0 ±0.43 | 3.22 / 2.6 ±1.54 |
| R | 10 | 2.07 / 1.98 ±0.37 | 1.82 / 1.54 ±0.66 | 91.14 / 86.74 ±22.59 | 8.89 / 5.25 ±8.42 | 4.71 / 4.83 ±0.75 | 2.0 / 1.8 ±0.61 |
| G | 5 | 2.05 / 2.14 ±0.55 | 1.36 / 1.23 ±0.94 | 30.05 / 30.64 ±8.29 | 18.42 / 4.4 ±32.67 | 6.33 / 5.91 ±1.65 | 0.88 / 0.8 ±0.36 |

## G — full GitHub repos

| Unit | nLOC | CC tot | CC/fn | Cog tot | Cog/fn | Smells | Smells/kLOC | Dup% | CBO mean | CBO max | Maint | Debt% |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| G001_dbeaver | 683199 | 119112 | 2.21 | 106157 | 1.97 | 27053 | 39.6 | 2.1 | 7.63 | 218 | A | 1.3 |
| G002_elasticsearch | 4716097 | 707978 | 2.14 | 409291 | 1.23 | 114871 | 24.36 | 7.5 | 8.35 | 361 | A | 0.8 |
| G003_guava | 515631 | 85452 | 1.58 | 34326 | 0.63 | 10021 | 19.43 | 76.7 | 4.27 | 83 | A | 0.5 |
| G004_spring_boot | 529754 | 80926 | 1.47 | 19727 | 0.36 | 16230 | 30.64 | 4.4 | 5.5 | 180 | A | 0.6 |
| G005_termux | 28299 | 6313 | 2.83 | 5843 | 2.62 | 1025 | 36.22 | 1.4 | 5.91 | 61 | A | 1.2 |

## R — curated modules

| Unit | nLOC | CC tot | CC/fn | Cog tot | Cog/fn | Smells | Smells/kLOC | Dup% | CBO mean | CBO max | Maint | Debt% |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| R001_module_java | 1397 | 333 | 1.84 | 248 | 1.37 | 122 | 87.33 | 6.9 | 4.29 | 18 | A | 2.1 |
| R002_module_java | 1757 | 391 | 1.74 | 343 | 1.52 | 145 | 82.53 | 24.8 | 5.68 | 13 | A | 1.5 |
| R003_module_java | 1586 | 314 | 2.01 | 242 | 1.55 | 186 | 117.28 | 2.2 | 4.42 | 20 | A | 3.0 |
| R004_module_java | 1646 | 377 | 1.97 | 282 | 1.48 | 223 | 135.48 | 1.6 | 5.42 | 21 | A | 3.1 |
| R005_module_java | 2065 | 391 | 1.75 | 278 | 1.25 | 108 | 52.3 | 0.6 | 3.31 | 20 | A | 1.3 |
| R006_module_java | 1763 | 319 | 1.99 | 277 | 1.73 | 174 | 98.7 | 2.5 | 5.38 | 18 | A | 2.2 |
| R007_module_java | 1395 | 323 | 2.5 | 327 | 2.53 | 120 | 86.02 | 12.9 | 5.06 | 14 | A | 1.8 |
| R008_module_java | 1557 | 305 | 2.36 | 272 | 2.11 | 140 | 89.92 | 3.6 | 3.83 | 15 | A | 1.8 |
| R009_module_java | 1625 | 365 | 1.74 | 276 | 1.31 | 140 | 86.15 | 17.5 | 4.88 | 16 | A | 1.6 |
| R010_module_java | 1400 | 328 | 2.83 | 385 | 3.32 | 106 | 75.71 | 16.3 | 4.78 | 15 | A | 1.6 |

## ALG — 50 synthetic DSA units (summary)

- **Cyclomatic total:** mean 12.12; max 045_red_black_tree_insertion (46), min 049_euclidean_algorithms (3)
- **Cognitive total:** mean 12.4; max 045_red_black_tree_insertion (64), min 049_euclidean_algorithms (1)
- **Code smells:** mean 6.22; max 026_prims_mst (22), min 008_disjoint_set_union_find (2)
- **CBO mean:** mean 1.86; max 032_ford_fulkerson_max_flow (3), min 015_open_addressing_linear_probing (1)

Full per-unit data: `metrics.csv`.
