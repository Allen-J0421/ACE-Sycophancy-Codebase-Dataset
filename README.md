# Directed-Graph Cycle Detection

A small, dependency-free Java library that detects a cycle in a directed graph
and reports the vertices that form it.

## API

- **`DirectedGraph`** — an immutable adjacency-list graph over vertices
  `0 .. vertices()-1`. Build one with `DirectedGraph.from(vertexCount, edges)`,
  where `edges` is an array of `{from, to}` pairs. Construction validates the
  vertex count and every endpoint.
- **`CycleDetector`** — depth-first cycle detection in `O(V + E)`:
  - `boolean hasCycle(DirectedGraph)` — whether any cycle exists.
  - `Optional<Cycle> findCycle(DirectedGraph)` — one cycle if present.
- **`Cycle`** — the result: a closed vertex walk such as `[0, 1, 2, 0]`
  (`toString()` renders it as `0 -> 1 -> 2 -> 0`). A self-loop on `v` is `[v, v]`.

## Example

```java
DirectedGraph graph = DirectedGraph.from(4, new int[][] {
    {0, 1}, {1, 2}, {2, 0}, {2, 3}
});

new CycleDetector().findCycle(graph)
    .ifPresentOrElse(
        cycle -> System.out.println("cycle: " + cycle),  // cycle: 0 -> 1 -> 2 -> 0
        ()    -> System.out.println("acyclic"));
```

Run the bundled demo:

```sh
javac *.java && java DetectCycleDemo
```

## Tests

```sh
./run-tests.sh
```

This fetches the JUnit 5 Platform Console Standalone jar into `lib/` on first
run and drives plain `javac` — no Maven or Gradle required.
