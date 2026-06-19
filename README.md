# Open Addressing Hash Map (Linear Probing)

A generic hash map implemented with open addressing and linear probing.

## Layout

```
src/
  main/java/hashmap/
    OpenAddressingTable.java          # shared open-addressing storage core (internal)
    OpenAddressingHashMap.java        # Map<K,V> adapter over the core
    OpenAddressingHashSet.java        # Set<E> adapter over the core
    Demo.java                         # runnable demonstration (main)
  test/java/hashmap/
    OpenAddressingHashMapTest.java    # dependency-free self-checking tests
    OpenAddressingHashSetTest.java    # self-checking tests for the set
  jmh/java/hashmap/bench/
    HashMapBenchmark.java             # JMH benchmarks vs java.util.HashMap
benchmarks/
  run.sh                              # self-contained JMH runner (no Maven needed)
pom.xml                               # Maven build for the benchmark jar
```

## Build & run

```sh
# compile everything
javac -d out src/main/java/hashmap/*.java src/test/java/hashmap/*.java

# run the tests (exits non-zero on failure)
java -cp out hashmap.OpenAddressingHashMapTest
java -cp out hashmap.OpenAddressingHashSetTest

# run the demo
java -cp out hashmap.Demo
```

## Benchmarking (JMH)

Two ways to run the benchmark harness; both compare `OpenAddressingHashMap`
head-to-head with `java.util.HashMap` over `getHit`, `getMiss`, `populate`, and
`iterate`, parameterised by `impl` and `size`.

```sh
# No build tool required: downloads JMH into ./lib, compiles, and runs.
benchmarks/run.sh                                   # full suite (slow, rigorous)
benchmarks/run.sh -p size=10000 -f 1 -wi 3 -i 5     # quick smoke run
benchmarks/run.sh getHit -rf json -rff out.json     # one benchmark, JSON output

# Or with Maven:
mvn clean package
java -jar target/benchmarks.jar -p size=10000 -f 1
```

### What the numbers say (and don't)

A representative quick run (size = 100,000; 1 fork, 2×0.3s warmup, 3×0.3s
measurement — **indicative only, not publication-grade**) showed:

| Benchmark  | OpenAddressing | java.util.HashMap |
|------------|---------------:|------------------:|
| `getHit`   | ~9.0 ns/op     | ~7.4 ns/op        |
| `getMiss`  | ~11.5 ns/op    | ~8.0 ns/op        |
| `iterate`  | ~1.02 ms/op    | ~1.05 ms/op       |
| `populate` | ~5.7 ms/op     | ~7.3 ms/op        |

Honest reading: this implementation is **not** a uniform win over the JDK. It is
competitive on iteration and ahead on bulk insertion, but **slower on lookups**,
especially misses (longer linear-probe chains versus the JDK's bucketed design,
plus `Integer` autoboxing on every comparison). `java.util.HashMap` is a heavily
tuned, decades-old implementation; matching it on every axis was never the goal.
The harness exists precisely so claims are measured rather than assumed — and the
measurement says "competitive, with trade-offs," not "faster." Run it on your own
hardware with the full settings before drawing conclusions; the error bars on the
quick `populate` run above are large enough that only the order of magnitude is
trustworthy.

## Design notes

- **Generic** `OpenAddressingHashMap<K, V>` rather than fixed `int → int`.
- **Struct-of-arrays storage**: entries live in two parallel arrays (`keys` and
  `values`) instead of an array of per-entry node objects. This removes a level
  of indirection (no node to allocate or dereference) and improves locality — a
  probe scans the contiguous `keys` array and only reads `values` once a key
  matches. Slot state is encoded in `keys` itself: `null` = empty, a private
  sentinel = tombstone, anything else = live; so occupancy depends only on the
  key, and a live entry may hold a `null` value.
- **Power-of-two table** with a spread hash, so the slot index is a bit-mask
  (`h & (len - 1)`) and negative hash codes are handled correctly.
- **Tombstones** mark deleted slots so probe chains stay intact; they are
  reclaimed on insertion and cleared on resize.
- **Automatic resizing** once occupied slots exceed the load factor (default
  `0.5`), keeping operations amortized `O(1)` — the original fixed-capacity
  version looped forever once full.
- **Implements `java.util.Map<K, V>`**: a drop-in replacement usable anywhere a
  `Map` is expected. It extends `AbstractMap` for the standard contracts
  (`equals`/`hashCode`/`toString`/`putAll`/`keySet`/`values` and the Java 8
  default methods such as `merge`, `compute`, `putIfAbsent`) but **overrides the
  hot path** (`get`/`put`/`remove`/`containsKey`/`size`/`clear`) with O(1)
  implementations so it never falls back to `AbstractMap`'s O(n) defaults.
- **Live, fail-fast views**: `entrySet()`, `keySet()`, and `values()` reflect and
  write back to the map (e.g. `keySet().remove(k)` deletes the mapping, and the
  entry-set iterator supports `remove()`). Structurally modifying the map during
  iteration throws `ConcurrentModificationException`.
- **Value semantics**: equality and hash code follow the `Map` contract, so an
  `OpenAddressingHashMap` compares equal to any other `Map` (e.g. a `TreeMap`)
  with the same mappings.
- **Shared core, two adapters**: the storage mechanics (keys/values arrays,
  hashing, probing, tombstones, resizing, fail-fast iteration) live in one
  package-private class, `OpenAddressingTable`. `OpenAddressingHashMap` and
  `OpenAddressingHashSet` are thin adapters that **compose** it and present it as a
  `Map` or `Set`. Composition is used rather than a shared superclass because both
  already extend `AbstractMap`/`AbstractSet` and Java has single inheritance.
- **`OpenAddressingHashSet<E>`**: a complete `java.util.Set` over the shared core.
  It constructs its table with value-tracking *disabled*, so — unlike a set that
  wraps a map with a dummy value (the `java.util.HashSet` approach) — it allocates
  **no** per-element value storage at all. It extends `AbstractSet`, so
  `equals`/`hashCode`/`addAll`/`removeAll`/`retainAll`/`toArray` come for free, and
  it compares equal to any `Set` (e.g. a `TreeSet`) with the same elements.
- `null` keys are rejected; `null` values are allowed and distinguished from
  absence via `containsKey`/`getOrDefault`.

### A note on performance, honestly

The struct-of-arrays layout removes the per-entry node object and the pointer
chase that comes with it, and keeps the probe loop scanning a single dense array.
Those are real wins. But two caveats are worth stating plainly:

- For **reference-typed** keys and values (the only thing Java generics allow),
  the arrays still hold references, not inlined data, so the cache-locality
  benefit is smaller than in a language with value types. The largest SoA gains
  would come from **primitive specialization** (e.g. an `int`-keyed variant with
  an `int[]`), which generics cannot express.
- No microbenchmark is included, because a credible one needs JMH (warm-up, dead-
  code elimination, etc.); ad-hoc timing loops would be misleading. Treat the
  improvement as architectural, not as a measured speedup.

### A note on what was *not* added

The probing scheme is intentionally **not** abstracted behind a pluggable
`ProbeStrategy` interface. There is exactly one scheme this class needs (it is
named for it), and an abstraction with a single implementation is speculative
generality that adds ceremony without value. If quadratic probing or double
hashing were ever genuinely required, that would be the time to introduce the
seam.

## What changed from the original

The original was a single file with int-only keys/values, a fixed capacity of
20, and `-1` overloaded as both the tombstone key and the "not found" value.
Fixes in this refactor:

- Java naming conventions and Javadoc throughout.
- Generics instead of `int → int`.
- Sentinel/tombstone handling that no longer reserves `-1`.
- Automatic resizing (no more infinite loop when full).
- Correct handling of negative keys (no `ArrayIndexOutOfBoundsException`).
- Fixed a duplicate-key bug when reinserting into a reclaimed slot.
- Demo separated from the data structure, plus a test suite including a
  differential test against `java.util.HashMap`.
