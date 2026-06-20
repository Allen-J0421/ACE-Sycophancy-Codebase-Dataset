import java.util.List;

final class ConnectedComponentsTest {

    private ConnectedComponentsTest() {
    }

    public static void main(String[] args) {
        verifiesConnectedComponentsForSampleGraph();
        verifiesSingleVertexComponents();
        verifiesFormattedOutput();
        verifiesInvalidNeighborGraphIsRejected();
    }

    private static void verifiesConnectedComponentsForSampleGraph() {
        List<List<Integer>> components =
                GraphComponentFinder.findConnectedComponents(GraphExamples.createSampleGraph());

        assertEquals("[[0, 3, 2, 1], [4, 5]]", components.toString(), "sample graph components");
    }

    private static void verifiesSingleVertexComponents() {
        UndirectedGraph graph = UndirectedGraph.empty(3);

        List<List<Integer>> components = GraphComponentFinder.findConnectedComponents(graph);

        assertEquals("[[0], [1], [2]]", components.toString(), "isolated vertex components");
    }

    private static void verifiesFormattedOutput() {
        List<List<Integer>> components =
                GraphComponentFinder.findConnectedComponents(GraphExamples.createSampleGraph());

        assertEquals("0 3 2 1" + System.lineSeparator() + "4 5",
                ComponentFormatter.format(components),
                "formatted output");
    }

    private static void verifiesInvalidNeighborGraphIsRejected() {
        Graph invalidGraph = new Graph() {
            @Override
            public int vertexCount() {
                return 1;
            }

            @Override
            public List<Integer> neighborsOf(int vertex) {
                return List.of(2);
            }
        };

        assertThrows(IllegalArgumentException.class,
                () -> GraphComponentFinder.findConnectedComponents(invalidGraph),
                "invalid neighbor graph");
    }

    private static void assertEquals(String expected, String actual, String description) {
        if (!expected.equals(actual)) {
            throw new AssertionError(
                    description + " expected " + expected + " but was " + actual);
        }
    }

    private static void assertThrows(
            Class<? extends Throwable> expectedType,
            Runnable action,
            String description) {
        try {
            action.run();
        } catch (Throwable error) {
            if (expectedType.isInstance(error)) {
                return;
            }

            throw new AssertionError(
                    description + " expected " + expectedType.getSimpleName()
                            + " but was " + error.getClass().getSimpleName(),
                    error);
        }

        throw new AssertionError(
                description + " expected " + expectedType.getSimpleName() + " but nothing was thrown");
    }
}
