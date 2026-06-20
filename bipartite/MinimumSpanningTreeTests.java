package bipartite;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Lightweight, dependency-free test harness for {@link MinimumSpanningTree},
 * matching the style of the other harnesses in this package. Runnable with
 * {@code java bipartite.MinimumSpanningTreeTests}; the process exits non-zero if
 * any case fails.
 */
public final class MinimumSpanningTreeTests {

    private static final double EPSILON = 1e-9;
    private static int failures = 0;
    private static final MinimumSpanningTree MST = new MinimumSpanningTree();

    public static void main(String[] args) {
        computesMinimumWeightSpanningTree();
        spansEveryVertexAcyclically();
        singleVertexHasEmptyTree();
        emptyGraphHasEmptyTree();
        reportsDisconnectedGraph();
        rejectsNullGraph();

        if (failures == 0) {
            System.out.println("All tests passed.");
        } else {
            System.out.println(failures + " test(s) failed.");
            System.exit(1);
        }
    }

    // A connected graph whose unique MST has weight 8 (edges 0-1, 1-2, 2-3, 3-4).
    private static WeightedUndirectedGraph connectedGraph() {
        return WeightedUndirectedGraph.of(5, List.of(
                new WeightedEdge(0, 1, 1.0),
                new WeightedEdge(1, 2, 1.0),
                new WeightedEdge(2, 3, 1.0),
                new WeightedEdge(0, 2, 3.0),
                new WeightedEdge(1, 3, 4.0),
                new WeightedEdge(3, 4, 5.0),
                new WeightedEdge(2, 4, 6.0)));
    }

    private static void computesMinimumWeightSpanningTree() {
        WeightedUndirectedGraph graph = connectedGraph();
        MinimumSpanningTreeResult result = MST.compute(graph);
        boolean ok = result instanceof MinimumSpanningTreeResult.Tree tree
                && Math.abs(tree.totalWeight() - 8.0) < EPSILON;
        check("computes the minimum total weight", ok);
    }

    private static void spansEveryVertexAcyclically() {
        WeightedUndirectedGraph graph = connectedGraph();
        MinimumSpanningTreeResult result = MST.compute(graph);
        boolean ok = result instanceof MinimumSpanningTreeResult.Tree tree
                && isSpanningTree(graph, tree);
        check("the result is a valid spanning tree", ok);
    }

    private static void singleVertexHasEmptyTree() {
        WeightedUndirectedGraph graph = WeightedUndirectedGraph.of(1, List.of());
        MinimumSpanningTreeResult result = MST.compute(graph);
        boolean ok = result instanceof MinimumSpanningTreeResult.Tree tree
                && tree.edges().isEmpty()
                && tree.totalWeight() == 0.0;
        check("a single vertex yields an empty tree", ok);
    }

    private static void emptyGraphHasEmptyTree() {
        WeightedUndirectedGraph graph = WeightedUndirectedGraph.of(0, List.of());
        MinimumSpanningTreeResult result = MST.compute(graph);
        boolean ok = result instanceof MinimumSpanningTreeResult.Tree tree && tree.edges().isEmpty();
        check("the empty graph yields an empty tree", ok);
    }

    private static void reportsDisconnectedGraph() {
        // Two components: {0,1} and {2,3}.
        WeightedUndirectedGraph graph = WeightedUndirectedGraph.of(4, List.of(
                new WeightedEdge(0, 1, 1.0), new WeightedEdge(2, 3, 1.0)));
        MinimumSpanningTreeResult result = MST.compute(graph);
        boolean ok = result instanceof MinimumSpanningTreeResult.Disconnected disconnected
                && disconnected.order() == 4
                && disconnected.reachableVertices().equals(List.of(0, 1));
        check("reports a disconnected graph with the reachable component", ok);
    }

    private static void rejectsNullGraph() {
        boolean threw;
        try {
            MST.compute(null);
            threw = false;
        } catch (NullPointerException expected) {
            threw = true;
        }
        check("rejects null graph", threw);
    }

    /**
     * Verifies the tree has {@code order - 1} edges, each existing in the graph
     * with the stated weight, that the edges connect every vertex, and that the
     * summed weights equal the reported total (connected + correct edge count
     * implies acyclic).
     */
    private static boolean isSpanningTree(WeightedUndirectedGraph graph,
                                          MinimumSpanningTreeResult.Tree tree) {
        int order = graph.order();
        List<WeightedEdge> edges = tree.edges();
        if (edges.size() != order - 1) {
            return false;
        }

        List<List<Integer>> adjacency = new ArrayList<>();
        for (int i = 0; i < order; i++) {
            adjacency.add(new ArrayList<>());
        }
        double total = 0.0;
        for (WeightedEdge edge : edges) {
            if (!edgeExists(graph, edge)) {
                return false;
            }
            total += edge.weight();
            adjacency.get(edge.u()).add(edge.v());
            adjacency.get(edge.v()).add(edge.u());
        }
        return Math.abs(total - tree.totalWeight()) < EPSILON && isConnected(adjacency, order);
    }

    private static boolean edgeExists(WeightedUndirectedGraph graph, WeightedEdge edge) {
        for (WeightedEdge candidate : graph.weightedNeighbors(edge.u())) {
            if (candidate.v() == edge.v() && candidate.weight() == edge.weight()) {
                return true;
            }
        }
        return false;
    }

    private static boolean isConnected(List<List<Integer>> adjacency, int order) {
        if (order == 0) {
            return true;
        }
        boolean[] seen = new boolean[order];
        Deque<Integer> queue = new ArrayDeque<>();
        queue.add(0);
        seen[0] = true;
        int count = 1;
        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (int v : adjacency.get(u)) {
                if (!seen[v]) {
                    seen[v] = true;
                    count++;
                    queue.add(v);
                }
            }
        }
        return count == order;
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
