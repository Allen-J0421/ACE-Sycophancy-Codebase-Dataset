package bipartite;

import java.util.List;

/**
 * Lightweight, dependency-free test harness for {@link TopologicalSort},
 * matching the style of {@link BipartiteCheckerTests} and
 * {@link DepthFirstSearchTests}. Runnable with
 * {@code java bipartite.TopologicalSortTests}; the process exits non-zero if any
 * case fails.
 */
public final class TopologicalSortTests {

    private static int failures = 0;
    private static final TopologicalSort SORT = new TopologicalSort();

    public static void main(String[] args) {
        sortsDiamondIntoDependencyOrder();
        respectsEdgeDirectionForEveryEdge();
        breaksTiesByAscendingVertexId();
        includesIsolatedVertices();
        emptyGraphSortsToEmptyOrder();
        detectsCycle();
        cycleWitnessIsAValidDirectedCycle();
        rejectsNullGraph();

        if (failures == 0) {
            System.out.println("All tests passed.");
        } else {
            System.out.println(failures + " test(s) failed.");
            System.exit(1);
        }
    }

    private static void sortsDiamondIntoDependencyOrder() {
        // 0 -> {1, 2} -> 3; the lexicographically smallest order is 0,1,2,3.
        DirectedGraph graph = DirectedGraph.of(4, new int[][] {{0, 1}, {0, 2}, {1, 3}, {2, 3}});
        TopologicalSortResult result = SORT.sort(graph);
        check("diamond sorts into the smallest dependency order",
                result instanceof TopologicalSortResult.Sorted sorted
                        && sorted.order().equals(List.of(0, 1, 2, 3)));
    }

    private static void respectsEdgeDirectionForEveryEdge() {
        DirectedGraph graph = DirectedGraph.of(6, new int[][] {
                {5, 2}, {5, 0}, {4, 0}, {4, 1}, {2, 3}, {3, 1}
        });
        TopologicalSortResult result = SORT.sort(graph);
        check("every edge points forward in the produced order",
                result instanceof TopologicalSortResult.Sorted sorted
                        && isValidTopologicalOrder(graph, sorted.order()));
    }

    private static void breaksTiesByAscendingVertexId() {
        // No edges: all vertices are sources, emitted smallest-first.
        DirectedGraph graph = DirectedGraph.of(3, new int[][] {});
        TopologicalSortResult result = SORT.sort(graph);
        check("independent vertices are ordered by ascending id",
                result instanceof TopologicalSortResult.Sorted sorted
                        && sorted.order().equals(List.of(0, 1, 2)));
    }

    private static void includesIsolatedVertices() {
        // Vertex 2 has no edges; it must still appear in the order.
        DirectedGraph graph = DirectedGraph.of(3, new int[][] {{0, 1}});
        TopologicalSortResult result = SORT.sort(graph);
        check("isolated vertices are included",
                result instanceof TopologicalSortResult.Sorted sorted
                        && sorted.order().size() == 3
                        && sorted.order().contains(2));
    }

    private static void emptyGraphSortsToEmptyOrder() {
        DirectedGraph graph = DirectedGraph.of(0, new int[][] {});
        TopologicalSortResult result = SORT.sort(graph);
        check("empty graph sorts to an empty order",
                result instanceof TopologicalSortResult.Sorted sorted && sorted.order().isEmpty());
    }

    private static void detectsCycle() {
        DirectedGraph graph = DirectedGraph.of(3, new int[][] {{0, 1}, {1, 2}, {2, 0}});
        check("a cyclic graph is reported as cyclic", !SORT.sort(graph).isAcyclic());
    }

    private static void cycleWitnessIsAValidDirectedCycle() {
        // Vertex 0 feeds into the cycle 1 -> 2 -> 3 -> 1.
        DirectedGraph graph = DirectedGraph.of(4, new int[][] {{0, 1}, {1, 2}, {2, 3}, {3, 1}});
        TopologicalSortResult result = SORT.sort(graph);
        check("cycle witness is a valid directed cycle",
                result instanceof TopologicalSortResult.Cyclic cyclic
                        && isValidDirectedCycle(graph, cyclic.cycle()));
    }

    private static void rejectsNullGraph() {
        boolean threw;
        try {
            SORT.sort(null);
            threw = false;
        } catch (NullPointerException expected) {
            threw = true;
        }
        check("rejects null graph", threw);
    }

    /**
     * A valid order lists every vertex exactly once and places the source of
     * each directed edge before its target.
     */
    private static boolean isValidTopologicalOrder(DirectedGraph graph, List<Integer> order) {
        int n = graph.order();
        if (order.size() != n) {
            return false;
        }
        int[] position = new int[n];
        boolean[] seen = new boolean[n];
        for (int i = 0; i < order.size(); i++) {
            int vertex = order.get(i);
            if (seen[vertex]) {
                return false;
            }
            seen[vertex] = true;
            position[vertex] = i;
        }
        for (int u = 0; u < n; u++) {
            for (int v : graph.neighbors(u)) {
                if (position[u] >= position[v]) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * A valid directed-cycle witness has length at least two and every
     * consecutive pair — including the closing edge from the last vertex back to
     * the first — is a directed edge in the graph.
     */
    private static boolean isValidDirectedCycle(DirectedGraph graph, List<Integer> cycle) {
        int n = cycle.size();
        if (n < 2) {
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

    private static void check(String description, boolean condition) {
        if (condition) {
            System.out.println("PASS: " + description);
        } else {
            System.out.println("FAIL: " + description);
            failures++;
        }
    }
}
