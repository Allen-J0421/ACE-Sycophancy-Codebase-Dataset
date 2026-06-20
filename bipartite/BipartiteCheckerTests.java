package bipartite;

import java.util.List;

/**
 * Lightweight, dependency-free test harness for the bipartite checker. The
 * project has no build system or test framework, so tests are plain assertions
 * runnable with {@code java bipartite.BipartiteCheckerTests}; the process exits
 * non-zero if any case fails.
 */
public final class BipartiteCheckerTests {

    private static int failures = 0;
    private static final BipartiteChecker CHECKER = new BipartiteChecker();

    public static void main(String[] args) {
        triangleIsNotBipartite();
        triangleWitnessIsAValidOddCycle();
        longOddCycleWitnessIsValid();
        evenCycleIsBipartite();
        emptyGraphIsBipartite();
        disconnectedGraphIsBipartite();
        partitionsCoverAllVertices();
        checkerRunsOnWeightedGraphViaInterface();
        weightedNeighborsCarryWeights();
        directedGraphStoresOneWayEdges();
        rejectsEdgeOutOfRange();
        rejectsMalformedEdgePair();
        rejectsSelfLoop();
        rejectsWeightedSelfLoop();
        rejectsNegativeVertexCount();
        rejectsNullGraph();

        if (failures == 0) {
            System.out.println("All tests passed.");
        } else {
            System.out.println(failures + " test(s) failed.");
            System.exit(1);
        }
    }

    private static void triangleIsNotBipartite() {
        UndirectedGraph graph = UndirectedGraph.of(3, new int[][] {{0, 1}, {1, 2}, {2, 0}});
        check("triangle is not bipartite", !CHECKER.check(graph).isBipartite());
    }

    private static void triangleWitnessIsAValidOddCycle() {
        UndirectedGraph graph = UndirectedGraph.of(3, new int[][] {{0, 1}, {1, 2}, {2, 0}});
        BipartiteResult result = CHECKER.check(graph);
        check("triangle witness is a valid odd cycle",
                result instanceof BipartiteResult.NotBipartite notBipartite
                        && isValidOddCycle(graph, notBipartite.oddCycle()));
    }

    private static void longOddCycleWitnessIsValid() {
        // A 5-cycle 0-1-2-3-4-0 (odd) plus a pendant vertex 5 hanging off it.
        UndirectedGraph graph = UndirectedGraph.of(6, new int[][] {
                {0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 0}, {2, 5}
        });
        BipartiteResult result = CHECKER.check(graph);
        check("longer odd-cycle witness is valid",
                result instanceof BipartiteResult.NotBipartite notBipartite
                        && isValidOddCycle(graph, notBipartite.oddCycle()));
    }

    private static void evenCycleIsBipartite() {
        UndirectedGraph graph = UndirectedGraph.of(4, new int[][] {{0, 1}, {1, 2}, {2, 3}, {3, 0}});
        check("even cycle is bipartite", CHECKER.check(graph).isBipartite());
    }

    private static void emptyGraphIsBipartite() {
        UndirectedGraph graph = UndirectedGraph.of(0, new int[][] {});
        check("empty graph is bipartite", CHECKER.check(graph).isBipartite());
    }

    private static void disconnectedGraphIsBipartite() {
        // Two separate edges: {0-1} and {2-3}; no path between components.
        UndirectedGraph graph = UndirectedGraph.of(4, new int[][] {{0, 1}, {2, 3}});
        check("disconnected graph is bipartite", CHECKER.check(graph).isBipartite());
    }

    private static void partitionsCoverAllVertices() {
        UndirectedGraph graph = UndirectedGraph.of(4, new int[][] {{0, 1}, {1, 2}, {2, 3}, {3, 0}});
        BipartiteResult result = CHECKER.check(graph);
        boolean ok = result instanceof BipartiteResult.Bipartite bipartite
                && bipartite.partitionA().size() + bipartite.partitionB().size() == 4
                && List.of(0, 2).equals(bipartite.partitionA())
                && List.of(1, 3).equals(bipartite.partitionB());
        check("partitions cover every vertex exactly once", ok);
    }

    private static void checkerRunsOnWeightedGraphViaInterface() {
        // A weighted 4-cycle is bipartite; the checker reaches it through the
        // Graph interface and ignores the weights.
        Graph graph = WeightedUndirectedGraph.of(4, List.of(
                new WeightedEdge(0, 1, 2.5),
                new WeightedEdge(1, 2, 1.0),
                new WeightedEdge(2, 3, 4.0),
                new WeightedEdge(3, 0, 0.5)));
        check("checker runs on a weighted graph via the Graph interface",
                CHECKER.check(graph).isBipartite());
    }

    private static void weightedNeighborsCarryWeights() {
        WeightedUndirectedGraph graph = WeightedUndirectedGraph.of(2,
                List.of(new WeightedEdge(0, 1, 3.5)));
        List<WeightedEdge> outgoing = graph.weightedNeighbors(0);
        boolean ok = outgoing.size() == 1
                && outgoing.get(0).u() == 0
                && outgoing.get(0).v() == 1
                && outgoing.get(0).weight() == 3.5
                // ... and the reciprocal incidence is stored on vertex 1.
                && graph.weightedNeighbors(1).equals(List.of(new WeightedEdge(1, 0, 3.5)));
        check("weighted neighbors carry weights symmetrically", ok);
    }

    private static void directedGraphStoresOneWayEdges() {
        DirectedGraph graph = DirectedGraph.of(2, new int[][] {{0, 1}});
        check("directed graph stores one-way edges",
                graph.neighbors(0).equals(List.of(1)) && graph.neighbors(1).isEmpty());
    }

    private static void rejectsWeightedSelfLoop() {
        check("rejects weighted self-loop edge",
                throwsIllegalArgument(() -> new WeightedEdge(1, 1, 2.0)));
    }

    private static void rejectsEdgeOutOfRange() {
        check("rejects edge referencing missing vertex",
                throwsIllegalArgument(() -> UndirectedGraph.of(2, new int[][] {{0, 5}})));
    }

    private static void rejectsMalformedEdgePair() {
        check("rejects edge pair without two endpoints",
                throwsIllegalArgument(() -> UndirectedGraph.of(2, new int[][] {{0}})));
    }

    private static void rejectsSelfLoop() {
        check("rejects self-loop edge",
                throwsIllegalArgument(() -> UndirectedGraph.of(2, new int[][] {{0, 0}})));
    }

    private static void rejectsNegativeVertexCount() {
        check("rejects negative vertex count",
                throwsIllegalArgument(() -> UndirectedGraph.of(-1, new int[][] {})));
    }

    private static void rejectsNullGraph() {
        boolean threw;
        try {
            CHECKER.check(null);
            threw = false;
        } catch (NullPointerException expected) {
            threw = true;
        }
        check("rejects null graph", threw);
    }

    /**
     * A witness is valid when it has odd length ({@code >= 3}) and every
     * consecutive pair of vertices — including the closing edge from the last
     * vertex back to the first — is actually adjacent in the graph.
     */
    private static boolean isValidOddCycle(UndirectedGraph graph, List<Integer> cycle) {
        int n = cycle.size();
        if (n < 3 || n % 2 == 0) {
            return false;
        }
        for (int i = 0; i < n; i++) {
            int from = cycle.get(i);
            int to = cycle.get((i + 1) % n);
            if (!graph.neighbors(from).contains(to)) {
                return false;
            }
        }
        return true;
    }

    private static boolean throwsIllegalArgument(Runnable action) {
        try {
            action.run();
            return false;
        } catch (IllegalArgumentException expected) {
            return true;
        }
    }

    private static void check(String description, boolean condition) {
        if (condition) {
            System.out.println("PASS: " + description);
        } else {
            System.out.println("FAIL: " + description);
            failures++;
        }
    }
}
