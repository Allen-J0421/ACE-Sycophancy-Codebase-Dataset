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
        Graph weightedCycle = WeightedUndirectedGraph.of(4, List.of(
                new WeightedEdge(0, 1, 2.5),
                new WeightedEdge(1, 2, 1.0),
                new WeightedEdge(2, 3, 4.0),
                new WeightedEdge(3, 0, 0.5)));
        report("Weighted undirected", checker.check(weightedCycle));

        // Directed — demonstrates one-way adjacency on the generic base.
        DirectedGraph directed = DirectedGraph.of(3, new int[][] {{0, 1}, {0, 2}, {1, 2}});
        System.out.println("Directed out-neighbors of 0: " + directed.neighbors(0));
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
