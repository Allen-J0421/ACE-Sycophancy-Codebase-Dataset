# Articulation Points

Finds the articulation points (cut vertices) of an undirected graph using
Tarjan's depth-first-search algorithm in `O(V + E)` time.

## Layout

| File | Responsibility |
|------|----------------|
| `Graph.java` | Undirected graph backed by an adjacency list, with validation |
| `ArticulationPointFinder.java` | Tarjan's algorithm; stateless and reusable |
| `Main.java` | Runnable demo on a small example graph |
| `ArticulationPointFinderTest.java` | Dependency-free test runner |

## Build and run

```sh
javac -d out *.java     # compile
java -cp out Main       # run the demo  -> prints "1 4"
java -cp out ArticulationPointFinderTest   # run the tests (exits non-zero on failure)
```

## Usage

```java
Graph graph = Graph.fromEdges(5, new int[][] {{0, 1}, {1, 4}, {2, 3}, {2, 4}, {3, 4}});
List<Integer> points = new ArticulationPointFinder().find(graph);  // [1, 4]
```

`find` returns the cut vertices in ascending order, or an empty list when the
graph has none.

## Multigraphs

Parallel edges and self-loops are supported and do not affect the result: a back
edge to a vertex's immediate parent only lowers its low-link to the parent's
discovery time, which the algorithm's `low[child] >= disc[parent]` test already
absorbs. This is verified directly (see the multigraph cases in
`ArticulationPointFinderTest`) and was cross-checked against a brute-force
component-counting oracle over 200k random multigraphs. (Note: *bridge*
detection, which uses a strict `>`, would require tracking edge identity — this
codebase does not implement bridges.)
