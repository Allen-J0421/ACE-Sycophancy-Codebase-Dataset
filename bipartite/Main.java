package bipartite;

import java.util.List;

/**
 * Demonstrates the bipartite checker running against several {@link Graph}
 * implementations through the shared interface: an unweighted undirected graph
 * (the original example), a weighted undirected graph, and a directed graph
 * (shown structurally, since bipartiteness applies to undirected graphs).
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        BipartiteChecker checker = new BipartiteChecker();

        // Unweighted undirected — the original example: the triangle 0-1-2 makes
        // it non-bipartite.
        Graph triangle = UndirectedGraph.of(4, new int[][] {{0, 1}, {0, 2}, {1, 2}, {2, 3}});
        report("Unweighted undirected", checker.check(triangle));

        // Weighted undirected — the same algorithm via the Graph interface; the
        // weights are simply ignored by the check.
        WeightedUndirectedGraph weightedCycle = WeightedUndirectedGraph.of(4, List.of(
                new WeightedEdge(0, 1, 2.5),
                new WeightedEdge(1, 2, 1.0),
                new WeightedEdge(2, 3, 4.0),
                new WeightedEdge(3, 0, 0.5)));
        report("Weighted undirected", checker.check(weightedCycle));

        // Directed — demonstrates one-way adjacency on the generic base.
        DirectedGraph directed = DirectedGraph.of(3, new int[][] {{0, 1}, {0, 2}, {1, 2}});
        System.out.println("Directed out-neighbors of 0: " + directed.neighbors(0));

        // Depth-first traversal over the same Graph interface.
        List<Integer> dfsOrder = new DepthFirstSearch().traverse(triangle);
        System.out.println("DFS preorder (unweighted undirected): " + dfsOrder);

        // Topological sort of the directed acyclic graph above.
        reportTopologicalSort(new TopologicalSort().sort(directed));

        // Dijkstra shortest path on the weighted graph: 0 -> 2 takes 0-1-2 (3.5)
        // rather than 0-3-2 (4.5).
        reportShortestPath(new ShortestPath().compute(weightedCycle, 0, 2));

        // Strongly connected components: the cycle 0-1-2 forms one SCC, 3 and 4
        // are their own.
        DirectedGraph withCycle = DirectedGraph.of(5, new int[][] {
                {0, 1}, {1, 2}, {2, 0}, {2, 3}, {3, 4}});
        StronglyConnectedComponentsResult scc =
                new StronglyConnectedComponents().compute(withCycle);
        System.out.println("Strongly connected components: " + scc.components());

        // Prim's minimum spanning tree on the weighted graph (drops the heaviest
        // edge of the cycle).
        reportMinimumSpanningTree(new MinimumSpanningTree().compute(weightedCycle));
    }

    private static void reportMinimumSpanningTree(MinimumSpanningTreeResult result) {
        switch (result) {
            case MinimumSpanningTreeResult.Tree tree -> System.out.println(
                    "Minimum spanning tree (weighted): totalWeight=" + tree.totalWeight()
                            + ", edges=" + tree.edges());
            case MinimumSpanningTreeResult.Disconnected disconnected -> System.out.println(
                    "Graph is disconnected; reachable component: "
                            + disconnected.reachableVertices());
        }
    }

    private static void reportShortestPath(ShortestPathResult result) {
        switch (result) {
            case ShortestPathResult.Path path -> System.out.println(
                    "Shortest path 0->2 (weighted): distance=" + path.distance()
                            + ", via=" + path.vertices());
            case ShortestPathResult.Unreachable unreachable -> System.out.println(
                    "No path from " + unreachable.source() + " to " + unreachable.target());
        }
    }

    private static void reportTopologicalSort(TopologicalSortResult result) {
        switch (result) {
            case TopologicalSortResult.Sorted sorted ->
                    System.out.println("Topological order (directed): " + sorted.order());
            case TopologicalSortResult.Cyclic cyclic ->
                    System.out.println("Topological sort failed; cycle witness: " + cyclic.cycle());
        }
    }

    private static void report(String label, BipartiteResult result) {
        switch (result) {
            case BipartiteResult.Bipartite bipartite -> System.out.println(
                    label + ": bipartite=true, A=" + bipartite.partitionA()
                            + ", B=" + bipartite.partitionB());
            case BipartiteResult.NotBipartite notBipartite -> System.out.println(
                    label + ": bipartite=false, odd-cycle witness=" + notBipartite.oddCycle());
        }
    }
}
