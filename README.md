# Graph Connectivity: Articulation Points & Bridges

Finds the **articulation points** (cut vertices) and **bridges** (cut edges) of an
undirected graph using Tarjan's depth-first-search algorithm. Both are derived
from the same low-link traversal in a single `O(V + E)` pass.

## Layout

| File | Responsibility |
|------|----------------|
| `Graph.java` | Undirected graph (adjacency list) with edge identity, validation |
| `GraphConnectivity.java` | Tarjan's algorithm; stateless, reusable, iterative |
| `ConnectivityResult.java` | Result holder: articulation points + bridges |
| `Main.java` | Runnable demo on a small example graph |
| `GraphConnectivityTest.java` | Dependency-free test runner |

## Build and run

```sh
javac -d out *.java     # compile
java -cp out Main       # run the demo
java -cp out GraphConnectivityTest   # run the tests (exits non-zero on failure)
```

The demo prints:

```
Articulation points: 1 4
Bridges: 0-1 1-4
```

## Usage

```java
Graph graph = Graph.fromEdges(5, new int[][] {{0, 1}, {1, 4}, {2, 3}, {2, 4}, {3, 4}});
ConnectivityResult result = new GraphConnectivity().analyze(graph);
result.articulationPoints();  // [1, 4]
result.bridges();             // [Edge[u=0, v=1, id=0], Edge[u=1, v=4, id=1]]
```

`articulationPoints()` returns the cut vertices in ascending order;
`bridges()` returns the cut edges ordered by endpoints then id. Both are empty
when the graph has none.

## Multigraphs

Parallel edges and self-loops are supported. Edges carry a stable identity
(`Graph.Edge.id()`), and the traversal skips only the specific tree edge it
arrived on — so a parallel edge back to the parent is treated as a genuine back
edge.

This distinction is invisible to articulation points (whose `low >= disc` test
absorbs it) but essential for bridges: a doubled edge keeps its endpoints
2-edge-connected, so the strict `low > disc` test correctly reports it as *not* a
bridge. Both outputs are verified directly (see the multigraph cases in
`GraphConnectivityTest`) and were cross-checked against a brute-force
component-counting oracle over 200k random multigraphs.
