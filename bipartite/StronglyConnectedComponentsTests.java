package bipartite;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Lightweight, dependency-free test harness for {@link StronglyConnectedComponents},
 * matching the style of the other harnesses in this package. Runnable with
 * {@code java bipartite.StronglyConnectedComponentsTests}; the process exits
 * non-zero if any case fails.
 */
public final class StronglyConnectedComponentsTests {

    private static int failures = 0;
    private static final StronglyConnectedComponents SCC = new StronglyConnectedComponents();

    public static void main(String[] args) {
        splitsCycleAndTailIntoComponents();
        singleCycleIsStronglyConnected();
        singleVertexIsStronglyConnected();
        dagYieldsSingletonComponents();
        emptyGraphHasNoComponents();
        componentsPartitionAllVertices();
        runsOnUndirectedGraphViaInterface();
        rejectsNullGraph();

        if (failures == 0) {
            System.out.println("All tests passed.");
        } else {
            System.out.println(failures + " test(s) failed.");
            System.exit(1);
        }
    }

    private static void splitsCycleAndTailIntoComponents() {
        // Cycle 0->1->2->0 with a tail 2->3.
        DirectedGraph graph = DirectedGraph.of(4, new int[][] {{0, 1}, {1, 2}, {2, 0}, {2, 3}});
        StronglyConnectedComponentsResult result = SCC.compute(graph);
        check("cycle-with-tail splits into the expected components",
                !result.isStronglyConnected()
                        && canonical(result).equals(List.of(List.of(0, 1, 2), List.of(3))));
    }

    private static void singleCycleIsStronglyConnected() {
        DirectedGraph graph = DirectedGraph.of(3, new int[][] {{0, 1}, {1, 2}, {2, 0}});
        StronglyConnectedComponentsResult result = SCC.compute(graph);
        check("a single cycle is strongly connected",
                result instanceof StronglyConnectedComponentsResult.StronglyConnected sc
                        && sc.component().equals(List.of(0, 1, 2)));
    }

    private static void singleVertexIsStronglyConnected() {
        DirectedGraph graph = DirectedGraph.of(1, new int[][] {});
        check("a single vertex is strongly connected",
                SCC.compute(graph).isStronglyConnected());
    }

    private static void dagYieldsSingletonComponents() {
        DirectedGraph graph = DirectedGraph.of(3, new int[][] {{0, 1}, {1, 2}});
        StronglyConnectedComponentsResult result = SCC.compute(graph);
        check("a DAG yields one singleton component per vertex",
                !result.isStronglyConnected()
                        && canonical(result).equals(List.of(List.of(0), List.of(1), List.of(2))));
    }

    private static void emptyGraphHasNoComponents() {
        DirectedGraph graph = DirectedGraph.of(0, new int[][] {});
        StronglyConnectedComponentsResult result = SCC.compute(graph);
        check("the empty graph has no components",
                !result.isStronglyConnected() && result.components().isEmpty());
    }

    private static void componentsPartitionAllVertices() {
        DirectedGraph graph = DirectedGraph.of(6, new int[][] {
                {0, 1}, {1, 2}, {2, 0},   // SCC {0,1,2}
                {3, 4}, {4, 3},           // SCC {3,4}
                {2, 3}, {4, 5}            // links and a tail {5}
        });
        check("components partition every vertex exactly once",
                isPartition(SCC.compute(graph), graph.order()));
    }

    private static void runsOnUndirectedGraphViaInterface() {
        // Through the Graph interface: an undirected graph's SCCs are its
        // connected components. Edge {0,1} plus isolated vertex 2.
        Graph graph = UndirectedGraph.of(3, new int[][] {{0, 1}});
        StronglyConnectedComponentsResult result = SCC.compute(graph);
        check("runs on an undirected graph via the Graph interface",
                canonical(result).equals(List.of(List.of(0, 1), List.of(2))));
    }

    private static void rejectsNullGraph() {
        boolean threw;
        try {
            SCC.compute(null);
            threw = false;
        } catch (NullPointerException expected) {
            threw = true;
        }
        check("rejects null graph", threw);
    }

    /** Returns the components in a canonical order (each sorted, then ordered by first id). */
    private static List<List<Integer>> canonical(StronglyConnectedComponentsResult result) {
        List<List<Integer>> components = new ArrayList<>(result.components());
        components.sort(Comparator.comparingInt(component -> component.get(0)));
        return components;
    }

    private static boolean isPartition(StronglyConnectedComponentsResult result, int order) {
        boolean[] seen = new boolean[order];
        int count = 0;
        for (List<Integer> component : result.components()) {
            for (int vertex : component) {
                if (vertex < 0 || vertex >= order || seen[vertex]) {
                    return false;
                }
                seen[vertex] = true;
                count++;
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
