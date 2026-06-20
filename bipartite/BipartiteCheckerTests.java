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
        evenCycleIsBipartite();
        emptyGraphIsBipartite();
        disconnectedGraphIsBipartite();
        partitionsCoverAllVertices();
        rejectsEdgeOutOfRange();
        rejectsMalformedEdgePair();
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
        int covered = result.partitionA().size() + result.partitionB().size();
        check("partitions cover every vertex exactly once", covered == 4
                && List.of(0, 2).equals(result.partitionA())
                && List.of(1, 3).equals(result.partitionB()));
    }

    private static void rejectsEdgeOutOfRange() {
        check("rejects edge referencing missing vertex",
                throwsIllegalArgument(() -> UndirectedGraph.of(2, new int[][] {{0, 5}})));
    }

    private static void rejectsMalformedEdgePair() {
        check("rejects edge pair without two endpoints",
                throwsIllegalArgument(() -> UndirectedGraph.of(2, new int[][] {{0}})));
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
