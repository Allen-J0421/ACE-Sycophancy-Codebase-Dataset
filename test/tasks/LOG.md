
## T03

| unit | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 |
|---|---|---|---|---|---|---|---|---|---|---|---|
| 030_strongly_connected_components | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS |
| 031_articulation_points | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS |

Non-PASS: none.

- 030: 56 graphs (baseline demo, edge cases, 40 seeded random). Oracle is a reference Tarjan; the partition is compared order-independently, and the component order must be topological (Kosaraju property of the baseline). Drives the baseline `kosaraju(int,int[][])` or the builder+finder API (vertices 0..V-1 added explicitly). Mutations caught: forward adjacency in pass 2 (31/56 FAIL), and forward finish order in the iteration-10 finder (31/56 FAIL).
- 031: 60 undirected graphs (demo, self-loops, parallel edges, disconnected graphs, an 800-vertex path and cycle, 45 seeded random). Oracle is a recursive low-link reference, cross-checked by brute-force vertex removal. The baseline returns `[-1]` when there are none; this is treated the same as `[]`. The baseline has no bridges, so they are checked for information only (all 60/60 at iterations 5-10). Mutations caught: `>=` changed to `>` (4/60 FAIL), and the root test `>1` changed to `>=1` (46/60 FAIL).

## T01

| unit | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 |
|---|---|---|---|---|---|---|---|---|---|---|---|
| 024_floyd_warshall | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS |
| 025_kruskals_mst | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS |
| 027_connected_components | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS |

Non-PASS: none.

Notes:
- 024: directed matrix, INF=(int)1e8 exactly; 47 graphs (demo + 6 edge cases + 40 random, negative edges via potential reweighting, no negative cycles; baseline defines no negative-cycle handling, iter1+ throws on them, which is out of domain). Iter 5+ path(i,j) also property-checked (15451 paths). Mutations (k from 1 in baseline; wrong next-hop in iter10) -> FAIL.
- 025: weight via (int, int[][]) at every iteration; baseline returns spanning-FOREST weight on disconnected graphs (tested). Iter1+ findMst(int, List<Edge>) also checked: totalWeight, spanning flag, acyclic chosen edges, and exact edge set on distinct-weight graphs. Iters 3-10 are empty commits (identical tree to iter 2). Mutations (early break; reversed comparator) -> FAIL.
- 027: order-independent partition vs union-find, plus exactly-once coverage and count/componentOf/connected/Component.size/contains cross-checks; iter 8+ run with default, BreadthFirstTraversal and DepthFirstTraversal. Mutations (skipped last vertex; truncated DFS neighbours; wrong componentOf index) -> FAIL.

## T06

| unit | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 |
|---|---|---|---|---|---|---|---|---|---|---|---|
| 047_cutting_a_rod | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS |
| 048_naive_pattern_searching | PASS | FAIL | FAIL | FAIL | FAIL | FAIL | FAIL | FAIL | FAIL | FAIL | FAIL |
| 049_euclidean_algorithms | PASS | FAIL | FAIL | FAIL | FAIL | FAIL | FAIL | FAIL | FAIL | FAIL | FAIL |
| 050_modular_exponentiation | PASS | FAIL | FAIL | FAIL | FAIL | FAIL | FAIL | FAIL | FAIL | FAIL | FAIL |

Non-PASS (each is a single defect introduced at iteration 1 and never touched again in iterations 2-10):
- 048 iters 1-10: baseline `search("", txt)` returns `[0..txt.length()]` (the loop runs i=0..n and the inner loop is empty). Iteration 1 added `if (pattern.isEmpty()) throw new IllegalArgumentException("pattern must not be empty")`, so `search("", "abc")` and `search("", "")` now throw. All 80 non-empty-pattern checks still pass. The main file is byte-identical in iterations 1-10.
- 049 iters 1-10: the sign convention for negative inputs changed. Baseline `findGCD(a,b)` (`a==0 ? b : findGCD(b%a, a)`) returns signed values such as `findGCD(4,-6) = -2`, `findGCD(0,-5) = -5` and `findGCD(-35,15) = -5`. Iteration 1 replaced it with `gcd(long,long)`/`gcdRecursive` that apply `Math.abs` (from iteration 3, `absExact`) and are "always non-negative". Of the 43 negative-input cases per method, the baseline returns a negative value in 27, and the iterations return the positive magnitude in all 27. The other 16 agree. Magnitudes are correct everywhere, and all 77 non-negative and zero cases per method pass against BigInteger.gcd.
- 050 iters 1-10: the domain narrowed through int overflow. Iteration 1 normalises the base with `long b = ((base % modulus) + modulus) % modulus;`, which is evaluated in 32-bit int. When `base % modulus + modulus > Integer.MAX_VALUE` (only possible for M > 2^30) it wraps: `powMod(2, MAX, MAX)` gives 0 (expected 2), and `powMod(MAX-1, 1, MAX)` gives -3 (expected MAX-1). The baseline does all arithmetic as `1L*a*b` and matches modPow for every int x>=0, n>=0, M>=1. Checks: 24/105 fail, all with M > 2^30. Every small-modulus case passes. Code is identical in iterations 1-10; iteration 4 only renamed the file and made the class public.

Notes:
- 047: 53 price tables: the demo (22), `{0}`, all-zero, single-best-piece, unit-pieces-best, 45 seeded random tables with up to 60 pieces, and one n=200 table. The oracle is an independent top-down memoised recursion. From iteration 1, both `maxRevenue(int[])` and `solve(int[]).maxRevenue` are scored. The reported cuts are also checked (sum to the rod length and earn the optimum), as WARN only; there were 0 warnings. Iterations 3-10 are empty commits. INFO only, out of domain: from iteration 1, price[0]!=0 throws, whereas the baseline ignores it. With all-negative prices, iteration 1 returns a negative revenue, whereas the baseline floors at 0. Mutations caught: `j<i` (12/14 FAIL) and `price[j-1]` (12/14 FAIL).
- 048: 82 cases: the demo `[0,9,12]`, overlapping matches (`aaa`/`aaaaa`, `abab`/`abababab`), pattern longer than the text (including empty text), pattern equal to the text, non-ASCII, empty pattern, and 70 seeded random cases over tiny alphabets. The oracle is an `indexOf(pat, from)` scan advancing by 1. Mutations caught: `i < n-m` (38/82 FAIL) and non-overlapping skip (25/82 FAIL).
- 049: every static gcd-named method with 2 int/long parameters is scored (`gcd` and `gcdRecursive` from iteration 1). There is no extended gcd at any iteration, so Bezout is not applicable. Per method there are 77 non-negative cases (the demo 35,15, zeros, MAX, a Fibonacci worst-case pair, and 60 seeded random) and 43 negative-input cases (13 fixed, including MIN_VALUE with 6, and 30 seeded random). Mutations caught: `a<=1` base case (55/120 FAIL) and a `Math.abs` sign change (27/120 FAIL, classified as sign-only).
- 050: 105 cases: the demo (1), fixed edge cases (x>=M, x==M, M=1 with n>0, n=MAX, and M near MAX), 30 small-modulus, 30 full-int-range, and 20 with a large modulus and large base. The point (n=0, M=1) is excluded because the baseline returns 1 and modPow returns 0; it is reported as INFO only. Negative x and n are outside the modPow domain. Mutations caught: int `x*x` (60/105 FAIL) and a skewed exponent halving (33/105 FAIL).

## T02

| unit | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 |
|---|---|---|---|---|---|---|---|---|---|---|---|
| 028_bipartite_graph | PASS | PASS | PASS | FAIL | FAIL | FAIL | FAIL | FAIL | FAIL | FAIL | FAIL |
| 029_detect_cycle_directed_graph | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS |

Non-PASS (one defect, introduced at iteration 3 and never reverted):
- 028 iters 3-10: self-loop input is now rejected. Baseline `isBipartite(V, edges)` accepts `{u,u}` and returns `false`, because `color[v]==color[u]` and a self-loop is a length-1 odd cycle. Iteration 3 (0b89483a) added `if (u == v) throw new IllegalArgumentException("Self-loops are not allowed ...")` to the `bipartite/Edge` record constructor. This is documented as deliberate ("callers ... should screen for self-loops"). `UndirectedGraph.of(int, int[][])` builds its graph through `Edge`, so any graph containing a self-loop now throws instead of answering. All 3 self-loop graphs fail. All 64 core graphs pass at every iteration, including the answer, the partitions, and the odd-cycle witness. Iterations 1-2 still accept self-loops, and iteration 2 returns a valid `[v]` witness.

Notes:
- 028: The driver tests only the bipartite contract. MST, SCC, topo sort, shortest path and DFS in `bipartite/` are ignored. The driver uses either the baseline `static isBipartite(int,int[][])` or `UndirectedGraph.of(int,int[][])` plus `new BipartiteChecker().check(g)`. Discovery prefers the Undirected factory over `DirectedGraph`/`Weighted*`. There are 67 graphs. 16 are fixed: the demo, V=0, a single vertex, all-isolated vertices, triangle, square, pentagon, K3,3, K4, parallel edges, and an odd cycle in a second component far from vertex 0. 48 are seeded random, from four generators: bipartite by construction, bipartite plus a planted odd cycle, random density, and multi-component with isolated vertices and parallel edges. The other 3 are self-loop graphs. The oracle is a parity union-find. When the result has `partitionA/partitionB`, they must be disjoint, cover every vertex, and every edge must cross them (31 partitions checked per run). When it has `oddCycle()`, it must be a simple odd cycle of real edges (33 checked from iteration 3, 36 at iterations 1-2 including self-loops). Mutations caught: the baseline coloring only from vertex 0 (9/64 core FAIL), the partitions returning A twice (30/64 FAIL), and the witness dropping its last vertex (33/64 FAIL).
- 029: Every exposed detector is tested on 65 directed graphs. 17 are fixed: the demo, V=0, a single vertex, a self-loop, a 2-cycle, parallel-edge DAGs, a cycle unreachable from vertex 0, tail-into-cycle, a cycle feeding a sink, two disjoint cycles, and a 1500-vertex chain and ring. 48 are seeded random: DAG, DAG plus a back edge, unconstrained digraphs with self-loops, and multi-component graphs. The detectors by iteration:
  - Iteration 0: `isCyclic(adj)`.
  - Iterations 1-4: `CycleDetector`.
  - Iterations 5-6: `DfsCycleDetector`, `KahnCycleDetector`, and `CycleDetectionAlgorithm.{DFS,KAHN}.create()`.
  - Iterations 7-10: the same four, plus `LoggingCycleDetector` wrapping each base detector with each logger (`NoOpLogger`, and `ConsoleLogger` on a null stream), for 8 in total.

  Both `hasCycle` and `findCycle` are scored against an iterative three-colour DFS oracle. Every returned cycle must be a simple directed cycle of real edges; a closed form `[a,..,a]` or an open form is accepted (200 cycles validated per run at iterations 7-10). `TopologicalSorter` (iteration 10) is not a detector and is not scored. Mutations caught: the baseline `visited < V-1` (3/65 FAIL), the Kahn `findCycle` keeping the walk's tail (3 invalid cycles), `LoggingCycleDetector.hasCycle` corrupted (FAIL on all 4 wrappers), and the iterative DFS never marking DONE (38/65 FAIL for DFS strategies only).

## T05 (044_avl_tree_insertion, 045_red_black_tree_insertion, 046_b_tree)

| unit | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 |
|---|---|---|---|---|---|---|---|---|---|---|---|
| 044_avl_tree_insertion | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS |
| 045_red_black_tree_insertion | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS |
| 046_b_tree | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS |

Non-PASS rows: none. No DRIVER_ERROR.

- **044 AVL.** The baseline has a static `Node insert(Node,int)` that ignores duplicates. Iterations 1-10 use an instance `AVLTree<T>.insert(T)` with a private root: iteration 6 adds a comparator, iteration 8 adds `remove` and derives the rotation case from balance factors, and iteration 10 adds `toTreeString`. The driver discovers either shape and reads `key/left/right/height` by reflection. It checks five things. The inorder equals the sorted distinct keys. Every node satisfies |bf| <= 1 and its stored height equals its real height. The preorder equals an independent reference AVL. Where present, `size/contains/inOrder/preOrder/iterator` agree. The demo prints `30 20 10 25 40 50`. It runs 52 sequences: the demo, edge cases, and 40 random sequences with duplicates. Mutations caught: the LR-case threshold changed to `>2` (balance violation), and duplicates inserted instead of ignored.
- **045 Red-black.** The baseline (the GfG recursive variant with `char colour`) keeps duplicates, sending them right. Iterations 1-10 move it to package `rbtree/` with a `Color` enum. From iteration 3 it is generic, iteration 4 uses an `InsertResult` return instead of flag fields (a record from iteration 7), and iteration 6 adds `toSortedList`. The driver checks five things. The inorder equals the sorted keys with duplicates kept. The root is black, there is no red-red pair, and black-height is equal on every path. The exact shape and colours match a port of the baseline algorithm, which is observable through `printTree`. `inorderTraversal/printTree/toSortedList` output matches. The full demo stdout matches the baseline. It runs 51 sequences plus the empty tree. Mutations caught: the uncle recolour dropped (red-red violation), and `<` changed to `<=` for duplicate placement (shape/printTree mismatch).
- **046 B-tree.** The ctor argument is the CLRS minimum degree t in every iteration. Iteration 1 renames it `minDegree` and rejects values below 2, iteration 7 moves node storage from arrays to Lists, and iteration 9 adds `delete` and extra demo lines, which the oracle does not score. The driver runs t=2..5 and checks five things. The in-order keys equal the sorted keys with duplicates kept. Key counts are 1..2t-1 at the root and t-1..2t-1 elsewhere, an internal node has keys+1 children, the leaf flag is consistent, and all leaves are at equal depth. Exactly 2t-1 keys fit in a single root and the 2t-th key splits it. `search`/`contains` hits and misses are correct. `traverse`/`toSortedList` output is sorted. The demo's first three lines match. Node layout against an exact port of the baseline is reported as NOTE only, with 0 notes at every iteration. It runs 76 sequences. Mutations caught: the wrong median promoted, a search using `>=`, and a split threshold of `2t-2`.

## T04 (040_bubble_sort, 041_quickselect, 042_balanced_parentheses, 043_circular_queue)

| unit | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 |
|---|---|---|---|---|---|---|---|---|---|---|---|
| 040_bubble_sort | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS |
| 041_quickselect | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS |
| 042_balanced_parentheses | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS |
| 043_circular_queue | PASS | FAIL | FAIL | FAIL | FAIL | FAIL | FAIL | FAIL | FAIL | FAIL | FAIL |

Non-PASS (no DRIVER_ERROR or COMPILE_ERROR):
- 043 iters 1-5 FAIL: the full/empty contract changed from the baseline. `myQueue` silently drops `enqueue` on full, and `dequeue`/`getFront`/`getRear` return -1 on empty. In `CircularQueue<E>`, `enqueue` throws IllegalStateException, and `dequeue`/`peek`/`peekRear` throw NoSuchElementException. Verified in CircularQueue.java at b95694066 through f948c683c.
- 043 iters 6-10 FAIL: the same change. `enqueue` is now an alias for `add`/`addLast` and throws IllegalStateException on full; `dequeue` is an alias for `remove`/`removeFirst` and throws NoSuchElementException on empty. `peek`/`peekRear` (peekFirst/peekLast from iter 8) now return null on empty, which is accepted as the sentinel. The new `offer`/`poll` return false/null, but the baseline-named operations throw. Verified in CircularQueue.java (bfdd72e56, 635878ba5, e772231cc) and CircularDeque.java (95cd36b03, 7d4471928). In every iteration, the core FIFO and wraparound behaviour (phase A) and the added Queue/Deque interface methods (phase C) match the bounded ArrayDeque model with 0 failures. The failures come only from the full/empty contract, which COMMON/T04 requires to match the baseline exactly.

Notes:
- 040: every iteration is driven with a whole-array sort; the baseline `bubbleSort(int[], n)` is called with n=len. The primitive path is compared with Arrays.sort over 70 arrays (demo, edge cases including MIN/MAX, and 60 seeded random arrays). From iter 1, every Sorter-shaped class (Bubble, and from iter 5 also Insertion, Merge and Quick) is checked with ascending and descending comparators. Stability is required for bubble, insertion and merge. From iter 8, the SortObserver path is checked with SortStats. Counts must be >= 0 and `comparisons()` must equal the comparator calls the driver itself counts. For bubble, `swaps()` must equal the inversion count and comparisons must fall in [n-1, n(n-1)/2]. `reset()` must return both counts to 0. Iters 2-4 have identical trees. Mutations caught: inner-loop off-by-one, outer-loop off-by-one in the baseline, SortStats double-counting swaps, and `>=` in the generic bubble (stability).
- 041: the baseline's k is 1-based k-th smallest, `kthSmallest(arr, 0, n-1, k)`. Iters 1-10 are identical and use `kthSmallest(int[], k)`, with a random pivot on a defensive copy. The driver checks 69 arrays with duplicates and extremes; small arrays use every k and large ones use a spread of k values, 803 checks in total. Each result is compared with sorted[k-1], and the caller's array must still be a permutation afterwards. Mutations caught: 0-based target in iter 10, and a corrupting swap in the baseline partition.
- 042: brackets are `()[]{}` and every other character is ignored, including `<>`, `«»`, CJK characters and whitespace. The checks are a 46-row hand-written truth table cross-checked against a reference stack, 80 random strings, and 60 generated balanced strings with noise plus a single-bracket mutant of each. Iter 10 adds an `isBalanced(String, Map)` overload, which is not scored. Mutations caught: the `]` matched against `{`, and the leftover-stack check removed.

## T08 (R003_module_java, R004_module_java)

| unit | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 |
|---|---|---|---|---|---|---|---|---|---|---|---|
| R003_module_java | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS |
| R004_module_java | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | FAIL | FAIL |

Oracle: both units use a **golden trace**, made deterministic as described below, plus property checks. There is no DRIVER_ERROR and no COMPILE_ERROR.

Non-PASS:
- R004 iters 9-10 FAIL (trace diverges at step 18): iteration 9 (7f5071aa2) changed the behaviour of `Disease.java`. The baseline's `PROPAGATION_RATE`/`LETHALITY_RATE` are **static**, so every `new Disease()` overwrites the rates of every existing disease. Iteration 9 made them per-instance fields, which changes which diseased animals die: the step-loop check `getPropagationRate() <= rand.nextDouble()` and the lethality check in `act`. It reads like a fix of a latent bug, but it is not behaviour-preserving. Verified: with only iteration 8's `Disease.java` swapped back in, both iter 9 and iter 10 match the golden trace exactly. Iteration 10 inherits the change.

Notes:
- **R003.** Simulator.java imports `javafx.application.Application` only to call `Application.launch(StatisticsView.class)`. unit.conf therefore sets `AUTO_EXCLUDE_IMPORTS=0`, excludes only `StatisticsView.java`, and uses no-op stubs: `stubs/javafx/application/Application.java` and a package-private `StatisticsView` in `stubs/StatisticsViewStub.java`. Randomness goes through the fixed-seed Randomizer, except `Gender.getRandom()`, which uses `Math.random()`. The driver re-seeds `Math$RandomNumberGeneratorHolder.randomNumberGenerator` before each run, which needs `--add-opens java.base/java.lang`. This made the trace deterministic, verified across separate JVMs. It uses a fresh classloader per run to reset static state. Grid is 60x80, run for 120 `simulateOneStep()` steps, run non-headless. Each step's trace line has per-class counts and a full-layout hash. The properties checked are: all 10 species present initially, occupancy in [0.5, 0.99], each grid occupant's `getLocation()` equal to its cell, the grid changes over time, and two runs are identical. All 10 iterations are RNG-order-exact refactors (SpeciesDescriptor, dead Fox/Rabbit/Animal removal, view/Time/Weather/Disease/Location cleanups). Mutation caught: `canBreed` `>=` changed to `>` diverges at step 1.
- **R004.** The only source of nondeterminism is `Weather.changeWeather` indexing `new HashSet<>(RAINING,SUNNY,CLOUDY).toArray()`, whose order depends on enum identity hashes. unit.conf runs with `-XX:+UnlockExperimentalVMOptions -XX:hashCode=2`, which gives a constant identity hash. The set then iterates in insertion order, which is also `WeatherType.values()` order, so iteration 8's switch to `WeatherType.values()` is judged fairly. The driver asserts the flag is active. It uses a fresh classloader per run, so the static HUNTER_COUNT, the Disease rates and `Hunter.alive` cannot leak between runs. It drives `Simulator(60,80)` with the static `playingSimulation` flag set and calls `simulateOneStep()` 100 times. `simulate(n)` loops forever while the field is viable at the baseline, so the driver does not use it. Each trace line has per-class counts, the diseased count and a layout hash. The properties checked are: 7 species present initially, occupancy in [0.12, 0.40], exactly 5 Hunters on the grid every step, location consistency (through the `location` field when `getLocation` is absent, as in the baseline Hunter), the grid changes, and determinism. Mutation caught: `incrementAge` `>` changed to `>=` diverges at step 1.

## T07 — R001_module_java, R002_module_java

| unit | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 |
|---|---|---|---|---|---|---|---|---|---|---|---|
| R001_module_java | PASS | FAIL | FAIL | FAIL | FAIL | FAIL | FAIL | FAIL | FAIL | FAIL | FAIL |
| R002_module_java | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS |

Oracles:
- R001: golden trace. The baseline is deterministic (shared Randomizer seed 1111; two in-process runs under fresh classloaders give the same result). The oracle is a 100-step per-step state trace on an 80x120 field: species counts, sick count, plant counts and stage sum, weather/season/humidity, day/night cycle and live-list size. Two runs must be identical and must match the golden trace.
- R002: smoke/property. The baseline is non-deterministic because Gender.getRandom() uses an unseeded `new Random()`. The driver runs 3 reps x (120 simulateOneStep + simulate(10)) on an 80x100 field. It checks: the exact step-0 census (populate uses only the seeded RNG, so this is deterministic); grid consistency (each occupant's getLocation() equals its cell, it is not removed, and it is tracked in the organism list); getHour() == (s/5)%24+1; no exceptions; counts are sane; at least 4 species survive (baseline: 6-8 over 45 reps); and the layout changes.
- Both drivers keep -Djava.awt.headless=true. In every R002 iteration, and in R001 iterations 0-6, the Simulator constructor builds the Swing JFrame views, which throws HeadlessException. At runtime each driver generates and compiles no-op look-alikes of every subject `java.awt.Window` subclass (same name, package, signatures and interfaces) and loads them ahead of `_classes`. The real model and the Simulator/engine orchestration still run. R001 iterations 5-10 get their SpeciesRegistry through the composition root: Main.standardSpecies(), or Main.buildRegistry(Main.loadConfig(..), new SpeciesCatalog()). EventPublisher and Logger get no-op proxies.
- Mutation checks, done in scratch, not in the repo. R001 Prey setFoodLevel(8)->7 gives FAIL at step 8. R002 inverted free-cell test in Field gives FAIL (species collapse). R002 hour off-by-one gives FAIL (clock).

Non-PASS:
- R001 iter1-10 FAIL: the trace diverges at step 1. The cause is two changes made in iteration 1 and carried unchanged into every later iteration. (a) `Animal.canBreed()` was reduced to `age >= getBreedingAge()`. That removed the baseline's `field.adjacentAnimalLocations(..)` call, whose result was dead but which still `Collections.shuffle`s with the shared seeded Random, so the RNG stream shifts. (b) `Simulator.simulateOneStep` went from iterator-remove-after-own-act to `removeIf(!isAlive)` after the loop. Animals killed after they acted now disappear the same step instead of staying one more step. In the baseline those carcasses act once more, calling becomeSick(), which draws RNG, and they count in sickPercentage. So both the trajectory and the displayed infection % change. Confirmed: re-adding both lines in scratch copies of iterations 1-10 makes every one of them match the golden trace exactly, so there are no other deviations.

## T09 (R005_module_java, R006_module_java)

Oracle types:
- R005_module_java: smoke/property. The baseline is NON-deterministic (Randomizer.useShared=false, Math.random() used for sex and temperature death), so there is no golden trace. The driver has two parts. Part B runs 30 rounds of 14 deterministic micro-scenarios on Animal/Predator/Plant (horde attack incl. the strict-> boundary, predation, grazing, overcrowding, starvation, night, hibernation cadence, aging, reproduction, plant bites/spread). Each outcome was derived by hand from the baseline code. Part A runs 3 end-to-end runs through Initializer.initializeSimulation, checked against CSV-derived initial counts, the season/day-night/temperature schedule and grid/list consistency invariants. Runs non-headless.
- R006_module_java: golden trace (deterministic, seeded Randomizer). A 100-step trace on the default 80x120 field records animal-layer and plant-layer counts per class, the infected-animal count and a 64-bit hash of the two-layer layout. It runs twice per subject to catch nondeterminism. The JavaFX Dashboard is replaced by a no-op stub (STUB_OVERRIDE=1). Runs non-headless. Note: R006 iterations 7-10 are empty commits. The task hint about "iteration 10 moved the plant-spring toggle out of Simulator" describes R005 iteration 10, not R006. R005's driver is unaffected, because it constructs Plant with the extra Habitat argument and checks the season schedule end-to-end.

| unit | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 |
|---|---|---|---|---|---|---|---|---|---|---|---|
| R005_module_java | PASS | PASS | PASS | FAIL | FAIL | FAIL | FAIL | FAIL | FAIL | FAIL | FAIL |
| R006_module_java | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS |

Non-PASS rows (all verified in Predator.checkForAttack):
- R005 iter3: horde food sharing changed. Iteration 3 changed `(Predator)neighboringAnimals.get(i)` to `get(j)` inside the j-loop. In the baseline every share of a horde kill goes to the first attacker found (two lions eat a tiger: food deltas {120,0}). Now the shares are spread ({80,40}). This fixes an apparent baseline typo, but it is an observable semantic change made during a behavior-preserving refactor.
- R005 iter4-10: horde strength rule changed. Iteration 4 also dropped the pre-add of the outer predator ("collect every member exactly once"). A horde's strength is now the plain sum instead of the baseline's attacker-counted-twice sum. A single lion (40) no longer kills an adjacent tiger (50), where the baseline gives 80 > 50. Horde members and shares change too (two lions: {60,60}). This persists through iteration 10. When B1/B2 are disabled in a scratch copy of the driver, iterations 3-10 pass every other check, so no other regression was found.

Mutation checks: R006 Plant.incrementAge `>`->`>=` -> FAIL at step 1. R005 findFoodAndEat without `break` -> FAIL B6; Habitat SEASON_CHANGE 50->40 -> FAIL part A season schedule; horde `>`->`>=` -> FAIL B3 boundary.

## T10 (audit of 18 previously-unreviewed drivers)

| unit | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 |
|---|---|---|---|---|---|---|---|---|---|---|---|
| 015_open_addressing_linear_probing | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS |
| 019_matrix_chain_multiplication | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS |
| 020_longest_common_subsequence | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS |
| 021_kmp_pattern_searching | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS |
| 022_rabin_karp | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS |
| 023_bellman_ford | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS |
| 026_prims_mst | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS |
| 032_ford_fulkerson_max_flow | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS |
| 033_huffman_coding | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS |
| 034_activity_selection | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS |
| 035_insertion_sort | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS |
| 036_heap_sort | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS |
| 037_counting_sort | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS |
| 038_radix_sort | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS |
| 039_bucket_sort | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS |
| R007_module_java | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS |
| R008_module_java | PASS | PASS | COMPILE_ERROR | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS |
| R010_module_java | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS | PASS |

Non-PASS:
- R008 iter 2 COMPILE_ERROR: a genuine defect, already verified and left alone as instructed. The agent timed out partway through a refactor, so `Giraffe.java:28` calls a `Prey` constructor that no longer exists.

No status changed after strengthening. Every driver calls the subject at every iteration, with no skip path that ends in PASS. Every oracle is independent of the subject. The mutations below were made in /tmp copies, never in the repo.

- 015 (strengthened): iterations 1-10 were only tested with a generic Map stress run, so they never got the baseline's inputs. Now every iteration also runs the baseline demo and the baseline int workload (seeds 99/7/2024, absent key maps to -1) through an adapter. Added a tombstone-heavy phase with 3 rounds of 400 puts and random removes, collision keys, several resizes and `entrySet().iterator().remove()`. Keys stay below 20, because the baseline's insert can create a duplicate after a tombstone on colliding keys, which is outside its demo domain. Mutations caught: no `size--` (FAIL), delete returns the key (FAIL), iter 10 reuses a tombstone without scanning on (FAIL), iter 10 lookup stops at a tombstone (FAIL, 390 checks).
- 019 (strengthened): added 15 longer and wider chains (n up to 25, dims up to 100), an all-ones chain and a 100/1 alternating chain. Mutations caught: `k<j-1` in the baseline (FAIL), inverted comparison at iter 10 (FAIL).
- 020 (strengthened): added 15 long inputs (40-300 chars) over binary, ACGT, a-z and non-ASCII alphabets, plus identical, reversed and substring cases. These exercise the Hirschberg and space-optimized strategies. Mutations caught: dropped max branch in the baseline (FAIL), Hirschberg base case `>=0` changed to `>0` at iter 10 (FAIL, 55/384).
- 021 (strengthened): patterns were at most 4 chars over {a,b}. Added 30 cases: periodic patterns with rich borders, patterns cut from the text, {a,b,c}, and a non-ASCII case. Mutations caught: LPS fallback `lps[len-2]` in the baseline (FAIL, caught only by the new cases), `>=textLength` guard at iter 10 (FAIL).
- 022 (strengthened): added 25 cases with patterns up to 30 chars, a-z, binary, chars above 255, pattern equal to the whole text, and matches at the last window. Mutations caught: wrong `h` exponent (FAIL), no match verification (FAIL), rolling stopped one window early at iter 10 (FAIL).
- 023 (strengthened): added worst-case chains (V=6/12/20) whose labels and edge order force all V-1 passes under both edge-order and vertex-order relaxation, including a negative cycle at the far end. Also added 20 larger graphs with parallel edges and self-loops. Path and cycle checks now use the minimum weight per pair. Mutations caught: detection on pass V-2 in the baseline (FAIL), V-2 passes at iter 10 (FAIL).
- 026 (strengthened): added 12 tie-heavy, complete and negative-weight graphs (V up to 30) plus an all-ties triangle. A structured result whose edge list cannot be read now fails instead of silently falling back to the weight-only check. Mutations caught: `<= key+1` in the baseline (FAIL), improvements of exactly 1 ignored at iter 10 (FAIL, 10/47).
- 032 (strengthened; there was a real oracle gap): dropping residual back-edges from the baseline still PASSED the old driver. Added a case where index-order BFS takes a blocking shortest path, so the correct answer needs flow cancellation, plus 30 more random networks with any source/sink and capacities up to 1000. The driver now also checks that the input matrix is not mutated. When the solver takes a pluggable path finder, every implementation is tested (BFS, DFS, and CapacityScaling from iter 7). Mutations caught: no back-edge in the baseline (now FAIL), CapacityScaling threshold `>=2` at iter 10 (FAIL; only reachable through the strategy loop).
- 033 (strengthened): added 15 cases with 17-60 symbols (beyond a-z, including punctuation and non-ASCII), heavy ties and frequencies up to 1e5. The exact deterministic-tree oracle is unchanged. Mutations caught: reversed index tie-break in the baseline (FAIL), `Math.max` merged index at iter 10 (FAIL).
- 034 (strengthened): added 20 instances with n=30-330, negative times and wide ranges. The input arrays must stay unmodified. Mutations caught: `>=` compatibility in the baseline (FAIL), sort by start at iter 10 (FAIL).
- 035/036 (strengthened): the driver now tests every sort entry point on the algorithm-named classes (int[], Comparable[], and T[] with `Comparator.naturalOrder()`), not just the first one, which adds the generic overloads at iters 2/6-10 (035) and 3-10 (036). Added 8 arrays of 500-2000 elements (few distinct values, full int range, sorted, reversed). Mutations caught: `j>0` in the baseline (FAIL), comparator-path bug at iter 10 (FAIL, only on the Comparator entry point), heapify loop `i>0` in the baseline (FAIL), right-child bound at iter 10 (FAIL).
- 037 (strengthened): added 8 arrays of 500-3500 elements (ranges 0-2 and 0-1e5) plus a spike at 65536, staying in the baseline's non-negative domain. Mutations caught: prefix-sum `<maxVal` in the baseline (FAIL), one occurrence dropped per value at iter 10 (FAIL).
- 038 (strengthened): now tests every int[] entry point on the class (`sort` and `sorted`). Added 6 arrays of 1000-5000 elements and a 10-digit case. Mutations caught: `m/exp>1` in the baseline (FAIL), no copy-back from the scratch buffer at iter 10 (FAIL).
- 039 (strengthened; infrastructure): the driver compiled the subject itself to work around `public class Main` in bucket_sort.java, which duplicated the runner's rules and scored a broken build as FAIL. The runner now auto-renames that file, so I deleted `unit.conf` (EXCLUDE) and `stubs/`, and the driver uses `_classes`. Added 9 cases: 500-3500 elements, 4 distinct values, values clustered near 0 and near 1, and extremes such as 0.99999994f and Float.MIN_VALUE. Mutations caught: `j>0` in the baseline (FAIL), large arrays skipped at iter 10 (FAIL).
- R007 / R008 / R010 (strengthened; the old smoke oracle was demonstrably too weak): the old R007 driver PASSED a baseline where Sharks never breed, and one where moves leave ghost occupants on the grid. All three drivers now check:
  - Grid consistency after every step: each live listed organism sits in bounds on its own cell, and the field holds exactly it there. Nothing stale is left on the grid; R008 checks this per Animal/Plant layer.
  - Life-cycle dynamics tracked by object identity: every species has births and deaths, aggregated over reps.
  - All species present at start (R010 previously accepted 7 of 8).

  Unit-specific notes:
  - R007: seeds `Math.random()` through `--add-opens` (unit.conf) and resets `Randomizer`, only so runs reproduce. Iterations 0, 5 and 10 turned out to give identical traces, but the oracle stays property-based.
  - R008: stays unseeded. Its randomness can't be seeded (`useShared=false`), so the checks were validated over 12 baseline reps, with at least 490 births/deaths for every species.
  - R010: the baseline's drought removes plants from the list but leaves them live on the grid, so the "no ghost" check there is "every field object is live and located at that cell". Direct steps don't advance the clock and species are only active during certain hours, so the driver sets the `time` field to 12 or 22 in 15-step blocks. It was validated over 10 baseline reps.

  Mutations caught: R007 Shark litter 0 (FAIL: no Shark born), R007 `setLocation` not clearing the old cell (FAIL: ghost), R007 iter 10 Seaweed placed but not listed (FAIL: ghost), R008 Lions never breed (FAIL), R008 `setLocation` not clearing (FAIL: ghost), R010 Snake young never listed (FAIL), R010 `setLocation` not clearing (FAIL).
