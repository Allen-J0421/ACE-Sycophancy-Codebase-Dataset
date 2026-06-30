# Baseline Code-Quality Metrics — 80 Units

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


## Per-category summary (mean / median)

| Category | n | Cyclomatic/fn | Cognitive/fn | Smells/kLOC | Duplication % | CBO mean | Debt % |
|---|---|---|---|---|---|---|---|
| ALG | 50 | 3.22 / 3.0 | 3.46 / 3.0 | 138.76 / 106.63 | 0.0 / 0.0 | 1.86 / 2.0 | 3.22 / 2.6 |
| R | 25 | 2.1 / 2.04 | 1.86 / 1.73 | 89.94 / 86.15 | 8.77 / 6.9 | 4.54 / 4.5 | 1.91 / 1.8 |
| G | 5 | 2.05 / 2.14 | 1.36 / 1.23 | 30.05 / 30.64 | 18.42 / 4.4 | 6.33 / 5.91 | 0.88 / 0.8 |

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
| R011_module_java | 1661 | 373 | 2.14 | 322 | 1.85 | 151 | 90.91 | 19.7 | 5.57 | 16 | A | 1.6 |
| R012_module_java | 1327 | 276 | 1.56 | 173 | 0.98 | 136 | 102.49 | 14.3 | 3.86 | 13 | A | 2.1 |
| R013_module_java | 1406 | 296 | 2.33 | 286 | 2.25 | 95 | 67.57 | 14.2 | 4.5 | 13 | A | 1.4 |
| R014_module_java | 1819 | 348 | 2.35 | 269 | 1.82 | 155 | 85.21 | 1.8 | 2.31 | 15 | A | 2.2 |
| R015_module_java | 1320 | 284 | 1.59 | 181 | 1.01 | 174 | 131.82 | 7.7 | 4.03 | 15 | A | 3.1 |
| R016_module_java | 1765 | 387 | 2.14 | 351 | 1.94 | 174 | 98.58 | 0.9 | 4.88 | 20 | A | 2.2 |
| R017_module_java | 1342 | 287 | 2.47 | 329 | 2.84 | 104 | 77.5 | 14.6 | 4.47 | 12 | A | 1.6 |
| R018_module_java | 1663 | 353 | 1.71 | 295 | 1.43 | 168 | 101.02 | 3.0 | 5.0 | 16 | A | 2.2 |
| R019_module_java | 1651 | 346 | 2.31 | 337 | 2.25 | 122 | 73.89 | 9.1 | 4.0 | 23 | A | 1.7 |
| R020_module_java | 2386 | 469 | 2.04 | 340 | 1.48 | 194 | 81.31 | 1.8 | 4.18 | 15 | A | 1.4 |
| R021_module_java | 1086 | 222 | 2.02 | 185 | 1.68 | 103 | 94.84 | 0.0 | 4.63 | 14 | A | 1.9 |
| R022_module_java | 3106 | 542 | 1.97 | 431 | 1.57 | 252 | 81.13 | 5.5 | 4.66 | 16 | A | 1.4 |
| R023_module_java | 1342 | 287 | 2.47 | 329 | 2.84 | 104 | 77.5 | 14.6 | 4.47 | 12 | A | 1.6 |
| R024_module_java | 1422 | 297 | 2.2 | 250 | 1.85 | 101 | 71.03 | 19.4 | 4.3 | 15 | A | 1.4 |
| R025_module_java | 1801 | 377 | 2.48 | 385 | 2.53 | 184 | 102.17 | 3.7 | 5.67 | 17 | A | 2.0 |

## ALG — 50 synthetic DSA units (summary)

- **Cyclomatic total:** mean 12.12; max 045_red_black_tree_insertion (46), min 049_euclidean_algorithms (3)
- **Cognitive total:** mean 12.4; max 045_red_black_tree_insertion (64), min 049_euclidean_algorithms (1)
- **Code smells:** mean 6.22; max 026_prims_mst (22), min 008_disjoint_set_union_find (2)
- **CBO mean:** mean 1.86; max 032_ford_fulkerson_max_flow (3), min 015_open_addressing_linear_probing (1)

Full per-unit data: `metrics.csv`.
