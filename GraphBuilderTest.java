import java.util.List;

/**
 * A small, dependency-free test runner for {@link GraphBuilder}. Run with
 * {@code java GraphBuilderTest}; it prints a summary and exits with a non-zero
 * status if any test fails.
 */
public final class GraphBuilderTest {

    private int passed;
    private int failed;

    public static void main(String[] args) {
        GraphBuilderTest tests = new GraphBuilderTest();
        tests.run();
        System.out.printf("%n%d passed, %d failed%n", tests.passed, tests.failed);
        if (tests.failed > 0) {
            System.exit(1);
        }
    }

    private void run() {
        undirectedBuilderMatchesFromEdges();
        directedBuilderMatchesFromEdges();
        fluentEdgesAndBatchAreEquivalent();
        autoSizesToHighestVertex();
        verticesSetsMinimumForIsolatedVertices();
        emptyBuilderProducesEmptyGraph();
        negativeEndpointIsRejected();
        negativeVertexCountIsRejected();
        malformedBatchEdgeIsRejected();
    }

    private void undirectedBuilderMatchesFromEdges() {
        int[][] edges = {{0, 1}, {1, 4}, {2, 3}, {2, 4}, {3, 4}};
        Graph built = GraphBuilder.undirected().edges(edges).build();
        Graph viaFactory = UndirectedGraph.fromEdges(5, edges);
        ConnectivityResult a = new GraphConnectivity().analyze(built);
        ConnectivityResult b = new GraphConnectivity().analyze(viaFactory);
        check("undirected builder matches fromEdges (AP)",
                a.articulationPoints(), b.articulationPoints());
        check("undirected builder matches fromEdges (bridges)", a.bridges(), b.bridges());
    }

    private void directedBuilderMatchesFromEdges() {
        int[][] arcs = {{1, 0}, {0, 2}, {2, 1}, {0, 3}, {3, 4}};
        Graph built = GraphBuilder.directed().edges(arcs).build();
        Graph viaFactory = DirectedGraph.fromEdges(5, arcs);
        check("directed builder matches fromEdges (SCC)",
                new StronglyConnectedComponents().find(built),
                new StronglyConnectedComponents().find(viaFactory));
    }

    private void fluentEdgesAndBatchAreEquivalent() {
        Graph fluent = GraphBuilder.directed().edge(0, 1).edge(1, 2).edge(2, 0).build();
        Graph batch = GraphBuilder.directed().edges(new int[][] {{0, 1}, {1, 2}, {2, 0}}).build();
        check("fluent and batch are equivalent",
                new StronglyConnectedComponents().find(fluent),
                new StronglyConnectedComponents().find(batch));
    }

    private void autoSizesToHighestVertex() {
        Graph graph = GraphBuilder.undirected().edge(0, 3).build();
        check("auto-sizes to highest vertex", graph.vertexCount(), 4);
    }

    private void verticesSetsMinimumForIsolatedVertices() {
        // One arc 0->1, but vertices(4) keeps 2 and 3 as isolated singletons.
        Graph graph = GraphBuilder.directed().edge(0, 1).vertices(4).build();
        check("vertices() includes isolated vertices",
                new StronglyConnectedComponents().find(graph),
                List.of(List.of(0), List.of(1), List.of(2), List.of(3)));
    }

    private void emptyBuilderProducesEmptyGraph() {
        Graph graph = GraphBuilder.undirected().build();
        check("empty builder produces empty graph", graph.vertexCount(), 0);
    }

    private void negativeEndpointIsRejected() {
        checkThrows("negative endpoint", IllegalArgumentException.class,
                () -> GraphBuilder.undirected().edge(0, -1));
    }

    private void negativeVertexCountIsRejected() {
        checkThrows("negative vertices()", IllegalArgumentException.class,
                () -> GraphBuilder.directed().vertices(-1));
    }

    private void malformedBatchEdgeIsRejected() {
        checkThrows("malformed batch edge", IllegalArgumentException.class,
                () -> GraphBuilder.undirected().edges(new int[][] {{0}}));
    }

    private void check(String name, Object actual, Object expected) {
        if (expected.equals(actual)) {
            passed++;
            System.out.println("PASS " + name);
        } else {
            failed++;
            System.out.println("FAIL " + name + ": expected " + expected + " but was " + actual);
        }
    }

    private void checkThrows(String name, Class<? extends Throwable> expected, Runnable action) {
        try {
            action.run();
            failed++;
            System.out.println("FAIL " + name + ": expected " + expected.getSimpleName()
                    + " but nothing was thrown");
        } catch (Throwable thrown) {
            if (expected.isInstance(thrown)) {
                passed++;
                System.out.println("PASS " + name);
            } else {
                failed++;
                System.out.println("FAIL " + name + ": expected " + expected.getSimpleName()
                        + " but threw " + thrown.getClass().getSimpleName());
            }
        }
    }
}
