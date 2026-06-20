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
        returnsReadOnlyComponents();
        copiesAdjacencyListsDefensively();
        builderCreatesDefensiveGraphSnapshot();
        rejectsInvalidEdges();
        exposesReadOnlyNeighbors();
    }

    private static void returnsReadOnlyComponents() {
        UndirectedGraph graph = UndirectedGraph.withVertexCount(2);
        graph.addEdge(0, 1);
        List<List<Integer>> components = ConnectedComponents.findComponents(graph);

        TestAssertions.assertThrows(
                UnsupportedOperationException.class,
                () -> components.add(List.of(2)),
                "read-only component list"
        );
        TestAssertions.assertThrows(
                UnsupportedOperationException.class,
                () -> components.get(0).add(2),
                "read-only component"
        );
    }

    @SuppressWarnings("deprecation")
    private static void keepsLegacyGetComponentsApi() {
        UndirectedGraph graph = UndirectedGraph.withVertexCount(2);
        graph.addEdge(0, 1);

        TestAssertions.assertEquals(
                List.of(List.of(0, 1)),
                ConnectedComponents.getComponents(graph),
                "legacy getComponents API"
        );
    }

    private static void findsComponentsInSampleGraph() {
        UndirectedGraph graph = UndirectedGraph.builder(6)
                .addEdge(1, 2)
                .addEdge(0, 3)
                .addEdge(2, 0)
                .addEdge(5, 4)
                .build();

        TestAssertions.assertEquals(
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

        TestAssertions.assertEquals(
                List.of(List.of(0, 1), List.of(2)),
                ConnectedComponents.findComponents(graph),
                "graph interface"
        );
    }

    private static void handlesIsolatedVertices() {
        UndirectedGraph graph = UndirectedGraph.withVertexCount(3);

        TestAssertions.assertEquals(
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

        TestAssertions.assertEquals(List.of(1), graph.neighbors(0), "defensive adjacency copy");
    }

    private static void builderCreatesDefensiveGraphSnapshot() {
        UndirectedGraph.Builder builder = UndirectedGraph.builder(3).addEdge(0, 1);
        UndirectedGraph graph = builder.build();

        builder.addEdge(1, 2);

        TestAssertions.assertEquals(List.of(1), graph.neighbors(0), "builder defensive graph snapshot");
        TestAssertions.assertEquals(List.of(0), graph.neighbors(1), "builder defensive neighbor snapshot");
    }

    private static void rejectsInvalidEdges() {
        UndirectedGraph graph = UndirectedGraph.withVertexCount(2);

        TestAssertions.assertThrows(
                IllegalArgumentException.class,
                () -> graph.addEdge(0, 2),
                "invalid edge"
        );
    }

    private static void exposesReadOnlyNeighbors() {
        UndirectedGraph graph = UndirectedGraph.withVertexCount(2);
        graph.addEdge(0, 1);

        TestAssertions.assertThrows(
                UnsupportedOperationException.class,
                () -> graph.neighbors(0).add(1),
                "read-only neighbors"
        );
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
