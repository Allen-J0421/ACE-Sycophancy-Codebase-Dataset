import java.util.ArrayList;
import java.util.List;

public final class ConnectedComponentsTest {

    private ConnectedComponentsTest() {
    }

    public static void main(String[] args) {
        findsComponentsInSampleGraph();
        keepsLegacyGetComponentsApi();
        worksWithGraphInterface();
        handlesIsolatedVertices();
        copiesAdjacencyListsDefensively();
        rejectsInvalidEdges();
        exposesReadOnlyNeighbors();
    }

    @SuppressWarnings("deprecation")
    private static void keepsLegacyGetComponentsApi() {
        UndirectedGraph graph = UndirectedGraph.withVertexCount(2);
        graph.addEdge(0, 1);

        assertEquals(
                List.of(List.of(0, 1)),
                ConnectedComponents.getComponents(graph),
                "legacy getComponents API"
        );
    }

    private static void findsComponentsInSampleGraph() {
        UndirectedGraph graph = UndirectedGraph.withVertexCount(6);
        graph.addEdge(1, 2);
        graph.addEdge(0, 3);
        graph.addEdge(2, 0);
        graph.addEdge(5, 4);

        assertEquals(
                List.of(List.of(0, 3, 2, 1), List.of(4, 5)),
                ConnectedComponents.findComponents(graph),
                "sample graph components"
        );
    }

    private static void worksWithGraphInterface() {
        Graph graph = new StaticGraph(List.of(
                List.of(1),
                List.of(0),
                List.of()
        ));

        assertEquals(
                List.of(List.of(0, 1), List.of(2)),
                ConnectedComponents.findComponents(graph),
                "graph interface"
        );
    }

    private static void handlesIsolatedVertices() {
        UndirectedGraph graph = UndirectedGraph.withVertexCount(3);

        assertEquals(
                List.of(List.of(0), List.of(1), List.of(2)),
                ConnectedComponents.findComponents(graph),
                "isolated vertices"
        );
    }

    private static void copiesAdjacencyListsDefensively() {
        List<List<Integer>> source = new ArrayList<>();
        source.add(new ArrayList<>(List.of(1)));
        source.add(new ArrayList<>(List.of(0)));

        UndirectedGraph graph = UndirectedGraph.fromAdjacencyList(source);
        source.get(0).clear();

        assertEquals(List.of(1), graph.neighbors(0), "defensive adjacency copy");
    }

    private static void rejectsInvalidEdges() {
        UndirectedGraph graph = UndirectedGraph.withVertexCount(2);

        assertThrows(
                IllegalArgumentException.class,
                () -> graph.addEdge(0, 2),
                "invalid edge"
        );
    }

    private static void exposesReadOnlyNeighbors() {
        UndirectedGraph graph = UndirectedGraph.withVertexCount(2);
        graph.addEdge(0, 1);

        assertThrows(
                UnsupportedOperationException.class,
                () -> graph.neighbors(0).add(1),
                "read-only neighbors"
        );
    }

    private static void assertEquals(Object expected, Object actual, String label) {
        if (!expected.equals(actual)) {
            throw new AssertionError(label + " expected <" + expected + "> but was <" + actual + ">");
        }
    }

    private static void assertThrows(
            Class<? extends RuntimeException> expectedType,
            Runnable action,
            String label
    ) {
        try {
            action.run();
        } catch (RuntimeException exception) {
            if (expectedType.isInstance(exception)) {
                return;
            }
            throw new AssertionError(label + " threw unexpected exception " + exception, exception);
        }

        throw new AssertionError(label + " did not throw " + expectedType.getSimpleName());
    }

    private static final class StaticGraph implements Graph {
        private final List<List<Integer>> adjacencyList;

        private StaticGraph(List<List<Integer>> adjacencyList) {
            this.adjacencyList = adjacencyList;
        }

        @Override
        public int vertexCount() {
            return adjacencyList.size();
        }

        @Override
        public Iterable<Integer> neighbors(int vertex) {
            return adjacencyList.get(vertex);
        }
    }
}
