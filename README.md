# Graph Connectivity

Connectivity algorithms for undirected and directed graphs, all built on
iterative Tarjan-style low-link depth-first search (each `O(V + E)`):

- **Undirected:** articulation points (cut vertices) and bridges (cut edges).
- **Directed:** strongly connected components (SCCs).

Undirected connectivity and SCCs are *separate* implementations on purpose:
articulation points/bridges and SCCs are defined on different graph types and do
not generalize into one another (modeling an undirected graph as a directed one
and running SCC just collapses each connected component into a single SCC). They
share the low-link DFS technique, not an algorithm.

## Layout

| File | Responsibility |
|------|----------------|
| `Graph.java` | Undirected graph (adjacency list) with edge identity, validation |
| `GraphConnectivity.java` | Articulation points + bridges; stateless, iterative |
| `ConnectivityResult.java` | Result holder: articulation points + bridges |
| `DirectedGraph.java` | Directed graph (out-adjacency list), validation |
| `StronglyConnectedComponents.java` | Tarjan's SCC algorithm; stateless, iterative |
| `Main.java` | Runnable demo of all three analyses |
| `GraphConnectivityTest.java` | Dependency-free test runner (undirected) |
| `StronglyConnectedComponentsTest.java` | Dependency-free test runner (SCC) |

## Build and run

```sh
javac -d out *.java     # compile
java -cp out Main       # run the demo
java -cp out GraphConnectivityTest            # undirected tests (exits non-zero on failure)
java -cp out StronglyConnectedComponentsTest  # SCC tests (exits non-zero on failure)
```

The demo prints:

```
Articulation points: 1 4
Bridges: 0-1 1-4
Strongly connected components: [0, 1, 2] [3] [4]
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

For directed graphs:

```java
DirectedGraph graph = DirectedGraph.fromEdges(5, new int[][] {{1, 0}, {0, 2}, {2, 1}, {0, 3}, {3, 4}});
List<List<Integer>> sccs = new StronglyConnectedComponents().find(graph);  // [[0, 1, 2], [3], [4]]
```

Each component lists its vertices in ascending order, and components are ordered
by their smallest vertex, so the result is deterministic.

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
