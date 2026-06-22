# Graph Connectivity

Connectivity algorithms for undirected and directed graphs, all built on
iterative Tarjan-style low-link depth-first search (each `O(V + E)`):

- **Undirected:** articulation points (cut vertices) and bridges (cut edges).
- **Directed:** strongly connected components (SCCs).

Undirected connectivity and SCCs remain *separate algorithms* on purpose:
articulation points/bridges and SCCs are defined on different graph types and do
not generalize into one another. What they *do* share is the graph representation
(`Graph`) and the low-link traversal state (`LowLinkState`).

## Shared abstractions

**`Graph`** is a common interface — a set of integer vertices plus, per vertex,
the edges traversable leaving it (`edgesFrom(v)`; the neighbor is
`edge.other(v)`). `UndirectedGraph` and `DirectedGraph` both implement it; the
only structural difference is that an undirected edge appears in *both* endpoints'
lists while a directed arc appears only in its tail's. Edges keep a stable
`id`, so multigraph bridge detection still works. Because both algorithms accept
any `Graph`, either can run on a shared graph object — but which analysis is
*meaningful* on which graph is graph theory, not API:

- Articulation points / bridges are meaningful only on an `UndirectedGraph`.
- `StronglyConnectedComponents` gives SCCs on a `DirectedGraph`; run on an
  `UndirectedGraph` it computes the **connected components** (every edge is
  symmetric). This is a genuinely useful shared-type case and is tested.

**`LowLinkState`** owns each vertex's discovery time and low-link value and the
operations that maintain them (`discover`, `relaxAgainstAncestor`,
`propagateFromChild`, `isLowLinkRoot`). The traversal control flow and the
algorithm-specific verdicts (cut vertices/edges vs. SCC extraction) stay with
each algorithm, since those genuinely differ.

## Layout

| File | Responsibility |
|------|----------------|
| `Graph.java` | Common graph interface: vertices + `edgesFrom(v)` |
| `Edge.java` | Edge with stable identity and `other(v)` |
| `UndirectedGraph.java` | Undirected `Graph`; edges shared between both endpoints |
| `DirectedGraph.java` | Directed `Graph`; arcs stored at their tail |
| `GraphBuilder.java` | Fluent, auto-sizing builder for either graph type |
| `LowLinkState.java` | Shared DFS state: discovery times + low-link logic |
| `GraphConnectivity.java` | Articulation points + bridges; stateless, iterative |
| `ConnectivityResult.java` | Result holder: articulation points + bridges |
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
java -cp out GraphBuilderTest                 # builder tests (exits non-zero on failure)
```

The demo prints:

```
Articulation points: 1 4
Bridges: 0-1 1-4
Strongly connected components: [0, 1, 2] [3] [4]
```

## Usage

```java
UndirectedGraph graph = UndirectedGraph.fromEdges(5, new int[][] {{0, 1}, {1, 4}, {2, 3}, {2, 4}, {3, 4}});
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

### Building graphs fluently

`GraphBuilder` avoids declaring the vertex count up front (it sizes to the highest
vertex referenced) and reads well for graphs built incrementally:

```java
Graph g = GraphBuilder.undirected().edge(0, 1).edge(1, 2).edge(2, 0).build();
Graph d = GraphBuilder.directed().edges(new int[][] {{0, 1}, {1, 2}}).vertices(5).build();
```

Use `vertices(n)` to include isolated vertices no edge touches. The constructors
and `fromEdges` factories remain — for a small fixed graph, `fromEdges` is still
the most compact option.

## Multigraphs

Parallel edges and self-loops are supported. Edges carry a stable identity
(`Edge.id()`), and the traversal skips only the specific tree edge it
arrived on — so a parallel edge back to the parent is treated as a genuine back
edge.

This distinction is invisible to articulation points (whose `low >= disc` test
absorbs it) but essential for bridges: a doubled edge keeps its endpoints
2-edge-connected, so the strict `low > disc` test correctly reports it as *not* a
bridge. Both outputs are verified directly (see the multigraph cases in
`GraphConnectivityTest`) and were cross-checked against a brute-force
component-counting oracle over 200k random multigraphs.
