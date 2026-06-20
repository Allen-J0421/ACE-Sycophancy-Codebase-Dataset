# Directed-Graph Cycle Detection

A small, dependency-free Java library that detects a cycle in a directed graph
and reports the vertices that form it.

## API

- **`DirectedGraph`** — an immutable adjacency-list graph over vertices
  `0 .. vertices()-1`. Construction validates the vertex count and every endpoint.
  Build one in any of three ways:

  ```java
  // 1. one-shot factory from an edge array
  DirectedGraph.from(4, new int[][] {{0, 1}, {1, 2}, {2, 0}, {2, 3}});

  // 2. fluent builder, fixed vertex count (endpoints validated against it)
  DirectedGraph.builder(4)
      .addEdge(0, 1).addEdge(1, 2).addEdge(2, 0).addEdge(2, 3)
      .build();

  // 3. fluent builder that grows to fit the edges (no need to pre-count)
  DirectedGraph.builder()
      .addEdges(new int[][] {{0, 1}, {1, 2}})
      .addEdge(2, 0)
      .ensureVertices(10)   // reserve isolated trailing vertices, if any
      .build();
  ```

  The builder is single-use (`build()` consumes it) and `from(...)` is just a
  shorthand for `builder(vertexCount).addEdges(edges).build()`.
- **`CycleDetector`** — the detection *interface* (`O(V + E)`):
  - `boolean hasCycle(DirectedGraph)` — whether any cycle exists.
  - `Optional<Cycle> findCycle(DirectedGraph)` — one cycle if present.
- **`Cycle`** — the result: a closed vertex walk such as `[0, 1, 2, 0]`
  (`toString()` renders it as `0 -> 1 -> 2 -> 0`). A self-loop on `v` is `[v, v]`.

### Choosing an algorithm at runtime

`CycleDetector` has two interchangeable implementations, selected through the
`CycleDetectionAlgorithm` enum:

| Algorithm | Strategy | Notes |
|-----------|----------|-------|
| `DFS`  | depth-first search, back-edge detection | iterative (explicit stack), so no recursion-depth limit; reconstructs the cycle path directly from the traversal |
| `KAHN` | Kahn's topological elimination          | decides *existence* cheaply; reconstructs a cycle from the residual core |

Both satisfy the same contract and agree on every result.

```java
// pick a concrete algorithm — e.g. from config or a CLI argument
CycleDetectionAlgorithm algorithm = CycleDetectionAlgorithm.valueOf(args[0]); // "DFS" | "KAHN"
CycleDetector detector = CycleDetector.create(algorithm);
```

### Logging events (optional)

The library is silent by default. To track events — when an algorithm begins,
when a cycle is found, when a graph is acyclic — attach a `CycleDetectionLogger`
with `withLogging(...)`:

```java
CycleDetector detector = CycleDetector.create(CycleDetectionAlgorithm.DFS)
    .withLogging(new ConsoleLogger());   // prints: [DFS] detection started ... / [DFS] cycle found: 0 -> 1 -> 2 -> 0
detector.findCycle(graph);
```

`CycleDetectionLogger` is a dependency-free facade. Built-in implementations:
`NoOpLogger` (the silent default) and `ConsoleLogger` (writes to a `PrintStream`).
The library depends only on the JDK, so to route events into `java.util.logging`,
SLF4J, Log4j, etc., implement the four-method interface yourself:

```java
CycleDetectionLogger toJul = new CycleDetectionLogger() {
    private final java.util.logging.Logger log = java.util.logging.Logger.getLogger("cycles");
    public void detectionStarted(String algo, DirectedGraph g) { log.info(algo + " started"); }
    public void cycleFound(String algo, Cycle c)               { log.info(algo + " cycle: " + c); }
    public void cycleDetected(String algo)                     { log.info(algo + " cycle present"); }
    public void noCycleFound(String algo)                      { log.info(algo + " acyclic"); }
};
```

Logging is added by a decorator (`LoggingCycleDetector`), so the algorithm
implementations themselves carry no logging code.

## Example

```java
DirectedGraph graph = DirectedGraph.from(4, new int[][] {
    {0, 1}, {1, 2}, {2, 0}, {2, 3}
});

CycleDetector detector = CycleDetector.create(CycleDetectionAlgorithm.DFS);
detector.findCycle(graph)
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
