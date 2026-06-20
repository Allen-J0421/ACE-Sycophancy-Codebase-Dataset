package bipartite;

import java.util.List;

/**
 * Lightweight, dependency-free test harness for {@link DepthFirstSearch},
 * matching the style of {@link BipartiteCheckerTests}. Runnable with
 * {@code java bipartite.DepthFirstSearchTests}; the process exits non-zero if
 * any case fails.
 */
public final class DepthFirstSearchTests {

    private static int failures = 0;
    private static final DepthFirstSearch DFS = new DepthFirstSearch();

    public static void main(String[] args) {
        traversesConnectedGraphInPreorder();
        traverseCoversDisconnectedComponents();
        traverseFromVisitsOnlyReachableVertices();
        emptyGraphYieldsEmptyOrder();
        runsOnWeightedGraphViaInterface();
        rejectsNullGraph();
        rejectsOutOfRangeSource();

        if (failures == 0) {
            System.out.println("All tests passed.");
        } else {
            System.out.println(failures + " test(s) failed.");
            System.exit(1);
        }
    }

    private static void traversesConnectedGraphInPreorder() {
        // Adjacency: 0->[1,2], 1->[0,3]; depth-first from 0 dives 0,1,3 then 2.
        UndirectedGraph graph = UndirectedGraph.of(4, new int[][] {{0, 1}, {0, 2}, {1, 3}});
        check("connected graph is visited in depth-first preorder",
                DFS.traverse(graph).equals(List.of(0, 1, 3, 2)));
    }

    private static void traverseCoversDisconnectedComponents() {
        // Two components {0-1} and {2-3}; traverse() restarts on the second.
        UndirectedGraph graph = UndirectedGraph.of(4, new int[][] {{0, 1}, {2, 3}});
        check("traverse covers every disconnected component",
                DFS.traverse(graph).equals(List.of(0, 1, 2, 3)));
    }

    private static void traverseFromVisitsOnlyReachableVertices() {
        UndirectedGraph graph = UndirectedGraph.of(4, new int[][] {{0, 1}, {2, 3}});
        check("traverseFrom visits only reachable vertices",
                DFS.traverseFrom(graph, 2).equals(List.of(2, 3)));
    }

    private static void emptyGraphYieldsEmptyOrder() {
        UndirectedGraph graph = UndirectedGraph.of(0, new int[][] {});
        check("empty graph yields an empty traversal", DFS.traverse(graph).isEmpty());
    }

    private static void runsOnWeightedGraphViaInterface() {
        // Reached through the Graph interface; weights are irrelevant to traversal.
        Graph graph = WeightedUndirectedGraph.of(3, List.of(
                new WeightedEdge(0, 1, 1.5), new WeightedEdge(1, 2, 2.5)));
        check("runs on a weighted graph via the Graph interface",
                DFS.traverse(graph).equals(List.of(0, 1, 2)));
    }

    private static void rejectsNullGraph() {
        boolean threw;
        try {
            DFS.traverse(null);
            threw = false;
        } catch (NullPointerException expected) {
            threw = true;
        }
        check("rejects null graph", threw);
    }

    private static void rejectsOutOfRangeSource() {
        UndirectedGraph graph = UndirectedGraph.of(2, new int[][] {{0, 1}});
        boolean threw;
        try {
            DFS.traverseFrom(graph, 5);
            threw = false;
        } catch (IndexOutOfBoundsException expected) {
            threw = true;
        }
        check("rejects an out-of-range source", threw);
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
