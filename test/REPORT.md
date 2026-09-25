# Correctness of claude-opus-4-8 experiment code: final results

**Scope.** This covers the claude-opus-4-8 (high, Agent) experiment branches for the 50 algorithm units and R001–R010. Each unit has 10 agent iterations, so there are **600 test subjects**, plus 60 baselines (iteration 0) used to check the tests themselves.

**Run.** The final full run (`bash test/run_all.sh`) finished on 2026-09-25 at 15:44, and its log is in `test/results/run_all.log`. It gave the same results as the previous run, so they are stable. All 60 baselines passed. No subject timed out, and none hit an error in the test harness itself.

---

## 1. Headline

| Scoring | What counts as a failure | PASS | Failed | Pass rate |
|---|---|---|---|---|
| **Strict** | Any behaviour that differs from the baseline on the baseline's own inputs | **531 / 600** | 69 | **88.5%** |
| **Broken code only** | Code that doesn't compile, or gives wrong answers on valid input | **589 / 600** | 11 | **98.2%** |

The 11 broken-code failures under the second scoring break down as:

| Kind | Subjects | Where |
|---|---|---|
| **Build broken** (does not compile) | 1 | R008 iteration 2 |
| **Wrong behaviour** (compiles, gives wrong answers) | 10 | 050 iterations 1–10 |

The other **58** strict failures are behaviour changes that compile and give defensible answers. The agent made them on purpose (new input validation, different return values) or as unrequested fixes to what look like baseline bugs. They break "behaviour-preserving", but they are not broken code. Section 4 explains each one.

---

## 2. Results by suite

| Suite | Subjects | Strict PASS | Build broken | Wrong behaviour | Behaviour change (not broken) | Broken-only PASS |
|---|---|---|---|---|---|---|
| 50 algorithm units | 500 | 452 (90.4%) | 0 | 10 | 38 | 490 (98.0%) |
| R001–R010 simulations | 100 | 79 (79.0%) | 1 | 0 | 20 | 99 (99.0%) |
| **Total** | **600** | **531 (88.5%)** | **1** | **10** | **58** | **589 (98.2%)** |

**By unit:**
- Strict scoring: **51 of 60 units** pass all 10 iterations.
- Broken-code-only scoring: **58 of 60 units** pass all 10 iterations. Only 050 and R008 do not.

---

## 3. Failures by iteration (turn)

Each iteration (turn) has 60 subjects, one per unit. Of these, 50 are algorithm units and 10 are simulations.

### 3.1 Failures at each iteration

| Iteration | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 | **Average per iteration** |
|---|---|---|---|---|---|---|---|---|---|---|---|
| **Strict failures** (of 60) | 5 | 6 | 7 | 7 | 7 | 7 | 7 | 7 | 8 | 8 | **6.9** |
| Strict failure rate | 8.3% | 10.0% | 11.7% | 11.7% | 11.7% | 11.7% | 11.7% | 11.7% | 13.3% | 13.3% | **11.5%** |
| – algorithm units (of 50) | 4 | 4 | 5 | 5 | 5 | 5 | 5 | 5 | 5 | 5 | **4.8** (9.6%) |
| – simulations (of 10) | 1 | 2 | 2 | 2 | 2 | 2 | 2 | 2 | 3 | 3 | **2.1** (21.0%) |
| **Broken-code failures** (of 60) | 1 | 2 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | **1.1** |
| Broken-code failure rate | 1.7% | 3.3% | 1.7% | 1.7% | 1.7% | 1.7% | 1.7% | 1.7% | 1.7% | 1.7% | **1.8%** |

### 3.2 Failures at each iteration, by category

| Iteration | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 | **Average** |
|---|---|---|---|---|---|---|---|---|---|---|---|
| Build broken (COMPILE_ERROR) | 0 | 1 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | **0.1** |
| Wrong behaviour (broken) | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | **1.0** |
| Deliberate contract change | 3 | 3 | 4 | 4 | 4 | 4 | 4 | 4 | 4 | 4 | **3.8** |
| Unrequested simulation change | 1 | 1 | 2 | 2 | 2 | 2 | 2 | 2 | 3 | 3 | **2.0** |
| **Total (strict)** | 5 | 6 | 7 | 7 | 7 | 7 | 7 | 7 | 8 | 8 | **6.9** |

### 3.3 When each failure first appeared

This counts units at the iteration where they first failed:

| Iteration | New failing units | Which |
|---|---|---|
| **1** | **5** | 043, 048, 049, 050, R001 |
| 2 | 1 | R008. This one was temporary: it was fixed at iteration 3 |
| 3 | 2 | 028, R005 |
| 4–8 | 0 | — |
| 9 | 1 | R004 |
| 10 | 0 | — |
| **Average** | **0.9 new failing units per iteration** | 9 in total over 10 iterations |

### 3.4 Averages per unit

- **All units:** under strict scoring, a unit fails **1.15 of its 10 iterations** on average (69 / 60). Under broken-code-only scoring, it's **0.18** (11 / 60).
- **The 9 units that failed at least once:** they fail **7.7 of 10 iterations** on average (69 / 9). That's because their failures started early and never went away.
- **Cumulative failure:** by iteration 10, **8 of 60 units (13.3%)** still differ from their baseline under strict scoring. Under broken-code-only scoring it's **1 of 60 (1.7%)**, which is 050.

### 3.5 What the pattern shows

- **Most deviations come at iteration 1.** 5 of the 9 failing units first fail there. That is when the agent does its first, largest refactor, and that's where most of the contract changes and the overflow bug were introduced.
- **Failures don't get fixed later.** Every behaviour change stayed until iteration 10, so the failure count only goes up (5 → 8). None of the 9 affected units went back to baseline behaviour.
- **The one exception:** R008's temporary build break, which the agent fixed in the next iteration.
- **050's bug** was introduced at iteration 1 and stayed for all nine later iterations, so the agent never noticed it.
- **The broken-code failure rate is flat at about 1.7% per iteration.** It rises only at iteration 2, because of R008.
- **Simulations fail at about twice the algorithm rate:** 21.0% of subjects per iteration versus 9.6%. Of the 21 simulation failures, 20 are behaviour changes and only 1 is broken code: R008's build break at iteration 2.

---

## 4. Why each subject failed

Every non-PASS below was confirmed in the subject's source code. Where the text says "proven", the baseline code was put back into the failing iteration and the test then passed, which shows that the named change is the whole cause.

### 4.1 Build broken (1 subject)

#### R008_module_java: iteration 2 (commit `124f41fb2`)

- **Commit:** the message is `claude-exp: iteration 2 [claude failed exit=124 timed_out=1]`. The agent run hit its time limit in the middle of a refactor.
- **What broke:** the constructor signature of `Prey` had already changed to `(boolean, Field, Location, boolean, boolean, int, int, double, int, int, int, double)`, but the subclasses had not been updated yet. `Giraffe.java:28` and `Lemur.java:28` still call `super(field, location, isInfected, isImmune)`. javac reports: *constructor Prey in class Prey cannot be applied to given types*.
- **Recovery:** iteration 3 (`c686b08c9`) compiles and passes, and so does every later iteration.
- **Note:** R002 iteration 2 and R004 iterations 3–4 are also labelled as timed-out commits, but they compile and pass.

### 4.2 Wrong behaviour (10 subjects)

#### 050_modular_exponentiation: iterations 1–10 (introduced in `4d825fac5`)

- **Where:** in `ModularExponentiation.powMod(int base, int exponent, int modulus)`, iteration 1 added a line to normalise the base:
  ```java
  long b = ((base % modulus) + modulus) % modulus;
  ```
- **The bug:** the right-hand side is evaluated in **32-bit `int`** and only widened to `long` afterwards. When `base % modulus + modulus > Integer.MAX_VALUE`, the sum wraps to a negative number. That can only happen when `modulus > 2^30`. The baseline does all its products as `1L * a * b` and is correct for every non-negative int input.
- **Wrong results:**

  | Call | Result | Expected |
  |---|---|---|
  | `powMod(2, 2147483647, 2147483647)` | 0 | 2 |
  | `powMod(2147483646, 1, 2147483647)` | -3 | 2147483646 |
  | `powMod(1073741824, 7, 1073754169)` | -499532758 | 711154810 |

- **Scale:** 24 of 105 checks fail, and all of them have a modulus above 2^30. Every small-modulus case is still correct.
- **Persistence:** the code is identical from iteration 1 to 10. Iteration 4 only renamed the file and made the class public, so the bug survived nine rounds of refactoring. The agent's own `ModularExponentiationTest.java` has the same line, so its tests could not catch it.
- **Why it counts as broken:** the method accepts these inputs and returns a wrong number with no error.

### 4.3 Behaviour changed, code not broken (58 subjects)

Nothing in this category is broken code. All of it compiles and gives answers that are reasonable on their own terms, but it no longer matches the baseline. Whether it counts as a failure depends on the scoring rule.

#### 4.3a Deliberate contract changes (38 subjects)

The agent deliberately changed what the code does at the edges of its input range. In most cases it also documented the new behaviour in the code.

| Unit | Iterations | Commit introduced | Baseline behaviour | New behaviour | What still passes |
|---|---|---|---|---|---|
| **028 bipartite graph** | 3–10 (8) | `0b89483a5` | `isBipartite` accepts a self-loop `{u,u}` and answers `false`, since a self-loop is an odd cycle | The `Edge` record constructor throws `IllegalArgumentException("Self-loops are not allowed …")`, so every graph containing a self-loop is rejected. The javadoc says callers "should screen for self-loops" | 64/64 graphs without self-loops, including partitions and odd-cycle witnesses; only the 3 self-loop graphs fail |
| **043 circular queue** | 1–10 (10) | `b95694066` | `enqueue` on a full queue is silently ignored; `dequeue`/`getFront`/`getRear` on an empty queue return `-1` | `enqueue` on full throws `IllegalStateException`; `dequeue` on empty throws `NoSuchElementException`. `peek` also throws at iterations 1–5 and returns `null` from iteration 6 (accepted). Iterations 6–10 add `offer`/`poll`, which don't throw, but the methods named in the baseline still do | FIFO order, wraparound and size are all correct at every iteration, and the added Queue/Deque methods match the reference model |
| **048 naive pattern search** | 1–10 (10) | `96c619a56` | `search("", txt)` returns `[0..txt.length()]`, the same as an `indexOf` scan | `if (pattern.isEmpty()) throw new IllegalArgumentException("pattern must not be empty")` | 80/80 non-empty-pattern checks, including overlapping matches; only the 2 empty-pattern checks fail |
| **049 Euclidean gcd** | 1–10 (10) | `b93081542` | `findGCD(a,b) = a==0 ? b : findGCD(b%a, a)` keeps the sign: `findGCD(4,-6) = -2`, `findGCD(0,-5) = -5` | `gcd`/`gcdRecursive` apply `Math.abs` (`absExact` from iteration 3) and are documented as "always non-negative" | The magnitude is correct everywhere, and 77/77 non-negative cases per method pass against `BigInteger.gcd`. 27 of the 43 negative-input cases per method differ only in sign |

**Judgment notes:**
- **049's** non-negative gcd is the standard mathematical convention.
- **043's** exceptions match `java.util.Queue` semantics.
- **048's** rejection is a common API choice.
- **028** is the most debatable of the four. A graph with a self-loop is a valid input with a well-defined answer, and the new code refuses to give one. So this change removes a capability rather than just tightening one.

#### 4.3b Unrequested changes to simulation behaviour (20 subjects)

The agent was asked for behaviour-preserving refactors. In these three units it also changed simulation logic, usually to fix what looks like a baseline bug. Each test replays a deterministic run step by step (or, for R005, hand-worked scenarios), so these changes are visible. In every case, putting the baseline lines back made every affected iteration pass.

| Unit | Iterations | Commit introduced | What changed | Effect | Evidence |
|---|---|---|---|---|---|
| **R001** | 1–10 (10) | `2d4b06480` | **(a)** `Animal.canBreed()` was reduced to `age >= getBreedingAge()`. That removed a call to `field.adjacentAnimalLocations(..)` whose result was unused, but which shuffled with the shared seeded random-number generator. **(b)** `Simulator.simulateOneStep` changed from removing each dead animal right after it acts to a single `removeIf(!isAlive)` after the loop | **(a)** shifts every later random draw. **(b)** means animals killed after their turn now disappear in the same step. In the baseline they acted once more (drawing random numbers in `becomeSick()`) and still counted towards the displayed infection %. The recorded trace differs from **step 1** | Proven: re-adding both baseline behaviours to scratch copies of iterations 1–10 makes all ten match the recorded trace for all 100 steps |
| **R004** | 9–10 (2) | `7f5071aa2` | `Disease.PROPAGATION_RATE` / `LETHALITY_RATE` changed from `static` to per-instance fields | In the baseline, every `new Disease()` overwrote the rates of every existing disease, which is a latent bug. Now each disease keeps its own rates, so different animals die. The recorded trace differs from **step 18** | Proven: swapping iteration 8's `Disease.java` into iterations 9 and 10 makes both match exactly |
| **R005** | 3–10 (8) | `a85509aa2` (iteration 3), `90fce1ab4` (iteration 4) | **Iteration 3:** in `Predator.checkForAttack`, `neighboringAnimals.get(i)` became `get(j)` inside the inner loop. **Iteration 4:** the first attacker is no longer added to the horde before the loop | **Iteration 3:** food from a horde kill is now spread across the horde instead of all going to the first attacker. Two lions eating a tiger now gain {80, 40} instead of {120, 0}. **Iterations 4–10:** horde strength is now a plain sum. The baseline counted the first attacker twice, so a single lion (40) killed an adjacent tiger (50) because 40+40 > 50. It no longer does | Confirmed in the code. With only the two horde checks disabled, iterations 3–10 pass every other check, so nothing else is hidden behind them |

**Judgment notes:** all three look like well-meant fixes. R004's static fields and R005's `get(i)` both read like baseline bugs, and R001's (b) is arguably more correct. But none of the three was requested, and each one silently changes the simulation's results. For a study of refactoring, that is a real finding, even though no code is broken.

---

## 5. Units that passed all 10 iterations under strict scoring (51)

**Algorithms (45):** 001–027, 029–042 and 044–047. That is every algorithm unit except 028, 043, 048, 049 and 050.

**Simulations (6):** R002, R003, R006, R007, R009, R010.

**Note:** these passes include units the agent reworked heavily. For example:
- 028's bipartite check became a 28-class package, and the non-bipartite parts of it pass too.
- 032 became a max-flow framework with pluggable path-finding strategies, and every strategy was tested.
- 016 became an LRU cache with pluggable eviction policies.

---

## 6. How the results were produced

The full design is in `test/README.md`, and the notes for each unit are in `test/tasks/LOG.md`.

1. **Subject selection.**
   - The subject for iteration *k* is the last commit on the experiment branch at or before iteration *k*. Iterations with no change have no commit of their own. For example, 015's iterations 1–7 all resolve to a single commit.
   - `test/manifest.tsv` lists every subject SHA, and `test/gen_manifest.py` regenerates it.
2. **Build.**
   - Each subject is extracted with `git archive` and compiled with javac 21.
   - Files that import JUnit, TestNG, JMH or JavaFX are skipped, because those libraries are not installed.
   - Some baselines declare a public class in a file with a different name, such as `public class HuffmanCoding` in `huffman_coding.java`. That is only a naming artifact, so the file is renamed to match before compiling.
   - Any remaining compile error scores the subject **COMPILE_ERROR**.
3. **Test program.**
   - Each unit has one test program (`test/units/<unit>/Driver.java`), used unchanged for all 11 iterations including the baseline.
   - It finds the algorithm by searching the compiled classes for a method with the right shape, so class and method renames don't break it.
   - Its expected answers never come from the agent's code. They come from a reference implementation, checkable properties, a model built on a trusted Java collection, or a recorded trace. All random inputs use fixed seeds.
4. **Simulations.** Two approaches were used:
   - **Recorded trace.** R001, R003, R004, R006 and R009 are deterministic, or were made deterministic by pinning their single random source. Each gets an exact 100-step record of species counts and a grid hash, and every iteration must reproduce it.
   - **Property checks.** R002, R005, R007, R008 and R010 get checks for grid consistency, births and deaths per species, the clock and season schedule, and running with no exceptions. R005 also has 14 hand-worked scenarios.
5. **Checking the tests themselves.**
   - Every baseline must pass.
   - Most tests were **mutation-checked**: a small bug was planted in a scratch copy of the baseline or iteration 10, and the test had to fail. Two tests that had passed planted bugs were strengthened: 032's back-edge removal, and a simulation where sharks never breed.
   - No failure was accepted until its cause was found in the subject code.

---

## 7. Limitations

- **Behaviour outside the baseline's input range is not scored.** Two things follow from this:
  - Anything the baseline never supported is not tested, for example negative inputs to counting sort.
  - Extra features the agent added are not scored, for example B-tree deletion from iteration 9. The one exception is the extra sorters in 040, which were checked for correctness.
- **Some tests have weaker evidence.** The tests for 001, 008–014 and 016–018 are sound in design (reference DP, model comparison, `Arrays.sort`), but nobody recorded a mutation check for them. They were written by agents that were cut off before reporting.
- **Five simulations only have property checks.** For R002, R005, R007, R008 and R010, a subtle numeric change could pass as long as the simulation stays internally consistent. The five simulations with recorded traces have no such gap.
- **Assigning failures to a category is a judgment call.** Section 4.3 separates deliberate or well-meant changes from broken code, and the evidence is given so readers can reclassify any of them. The strict number (88.5%) doesn't depend on any of these judgments.

---

## Appendix A: Full matrix (final run)

`.` = PASS · `F` = FAIL (behaviour differs from baseline) · `C` = COMPILE_ERROR. Column 0 is the baseline.

```
unit                                   0 1 2 3 4 5 6 7 8 9 10
001_binary_search                      . . . . . . . . . . .
002_two_pointers_technique             . . . . . . . . . . .
003_prefix_sum_array                   . . . . . . . . . . .
004_breadth_first_search               . . . . . . . . . . .
005_depth_first_search                 . . . . . . . . . . .
006_topological_sorting_bfs            . . . . . . . . . . .
007_dijkstras_shortest_path            . . . . . . . . . . .
008_disjoint_set_union_find            . . . . . . . . . . .
009_merge_sort                         . . . . . . . . . . .
010_quick_sort                         . . . . . . . . . . .
011_binary_heap                        . . . . . . . . . . .
012_building_heap_from_array           . . . . . . . . . . .
013_binary_search_tree_searching       . . . . . . . . . . .
014_trie_insert_and_search             . . . . . . . . . . .
015_open_addressing_linear_probing     . . . . . . . . . . .
016_lru_cache                          . . . . . . . . . . .
017_knapsack_01                        . . . . . . . . . . .
018_coin_change                        . . . . . . . . . . .
019_matrix_chain_multiplication        . . . . . . . . . . .
020_longest_common_subsequence         . . . . . . . . . . .
021_kmp_pattern_searching              . . . . . . . . . . .
022_rabin_karp                         . . . . . . . . . . .
023_bellman_ford                       . . . . . . . . . . .
024_floyd_warshall                     . . . . . . . . . . .
025_kruskals_mst                       . . . . . . . . . . .
026_prims_mst                          . . . . . . . . . . .
027_connected_components               . . . . . . . . . . .
028_bipartite_graph                    . . . F F F F F F F F
029_detect_cycle_directed_graph        . . . . . . . . . . .
030_strongly_connected_components      . . . . . . . . . . .
031_articulation_points                . . . . . . . . . . .
032_ford_fulkerson_max_flow            . . . . . . . . . . .
033_huffman_coding                     . . . . . . . . . . .
034_activity_selection                 . . . . . . . . . . .
035_insertion_sort                     . . . . . . . . . . .
036_heap_sort                          . . . . . . . . . . .
037_counting_sort                      . . . . . . . . . . .
038_radix_sort                         . . . . . . . . . . .
039_bucket_sort                        . . . . . . . . . . .
040_bubble_sort                        . . . . . . . . . . .
041_quickselect                        . . . . . . . . . . .
042_balanced_parentheses               . . . . . . . . . . .
043_circular_queue                     . F F F F F F F F F F
044_avl_tree_insertion                 . . . . . . . . . . .
045_red_black_tree_insertion           . . . . . . . . . . .
046_b_tree                             . . . . . . . . . . .
047_cutting_a_rod                      . . . . . . . . . . .
048_naive_pattern_searching            . F F F F F F F F F F
049_euclidean_algorithms               . F F F F F F F F F F
050_modular_exponentiation             . F F F F F F F F F F
R001_module_java                       . F F F F F F F F F F
R002_module_java                       . . . . . . . . . . .
R003_module_java                       . . . . . . . . . . .
R004_module_java                       . . . . . . . . . F F
R005_module_java                       . . . F F F F F F F F
R006_module_java                       . . . . . . . . . . .
R007_module_java                       . . . . . . . . . . .
R008_module_java                       . . C . . . . . . . .
R009_module_java                       . . . . . . . . . . .
R010_module_java                       . . . . . . . . . . .
```

## Appendix B: Classification of every non-PASS subject

| Unit | Iterations | Strict | Broken-code-only | Category |
|---|---|---|---|---|
| R008 | 2 | FAIL (COMPILE_ERROR) | **FAIL: build broken** | Agent timed out mid-refactor |
| 050 | 1–10 | FAIL | **FAIL: wrong behaviour** | int overflow gives wrong answers for modulus > 2^30 |
| 028 | 3–10 | FAIL | PASS | Deliberate contract change: rejects self-loops |
| 043 | 1–10 | FAIL | PASS | Deliberate contract change: throws on full or empty |
| 048 | 1–10 | FAIL | PASS | Deliberate contract change: rejects empty pattern |
| 049 | 1–10 | FAIL | PASS | Deliberate contract change: gcd always non-negative |
| R001 | 1–10 | FAIL | PASS | Unrequested behaviour change: random-draw order and death timing |
| R004 | 9–10 | FAIL | PASS | Unrequested behaviour change: per-disease rates |
| R005 | 3–10 | FAIL | PASS | Unrequested behaviour change: horde food split and attack strength |

## Appendix C: Reproducing the results

```sh
bash test/run_all.sh                                   # every subject + summary matrix
python3 test/lib/runner.py --unit 050_modular_exponentiation   # one unit
python3 test/lib/summarize.py                          # matrix from existing results
```

Details for each iteration are in `test/results/<unit>.tsv`, as the columns unit, iter, sha, status and detail.
