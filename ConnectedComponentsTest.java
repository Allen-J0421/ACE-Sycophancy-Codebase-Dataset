import java.util.List;

final class ConnectedComponentsTest {

    private ConnectedComponentsTest() {
    }

    public static void main(String[] args) {
        verifiesConnectedComponentsForSampleGraph();
        verifiesSingleVertexComponents();
        verifiesFormattedOutput();
        verifiesResultIsImmutable();
        verifiesInvalidNeighborGraphIsRejected();
    }

    private static void verifiesConnectedComponentsForSampleGraph() {
        ConnectedComponentsResult result =
                GraphComponentFinder.findConnectedComponents(GraphExamples.createSampleGraph());

        assertEquals(2, result.componentCount(), "sample graph component count");
        assertListEquals(List.of(0, 3, 2, 1), result.componentAt(0).vertices(), "first sample component");
        assertListEquals(List.of(4, 5), result.componentAt(1).vertices(), "second sample component");
    }

    private static void verifiesSingleVertexComponents() {
        UndirectedGraph graph = UndirectedGraph.empty(3);

        ConnectedComponentsResult result = GraphComponentFinder.findConnectedComponents(graph);

        assertEquals(3, result.componentCount(), "isolated vertex component count");
        assertListEquals(List.of(0), result.componentAt(0).vertices(), "first isolated component");
        assertListEquals(List.of(1), result.componentAt(1).vertices(), "second isolated component");
        assertListEquals(List.of(2), result.componentAt(2).vertices(), "third isolated component");
    }

    private static void verifiesFormattedOutput() {
        ConnectedComponentsResult result =
                GraphComponentFinder.findConnectedComponents(GraphExamples.createSampleGraph());

        assertEquals("0 3 2 1" + System.lineSeparator() + "4 5",
                result.format(),
                "formatted output");
    }

    private static void verifiesResultIsImmutable() {
        ConnectedComponentsResult result =
                GraphComponentFinder.findConnectedComponents(GraphExamples.createSampleGraph());

        assertThrows(UnsupportedOperationException.class,
                () -> result.components().add(new ConnectedComponent(List.of(99))),
                "result component list immutability");
        assertThrows(UnsupportedOperationException.class,
                () -> result.componentAt(0).vertices().add(99),
                "component vertex list immutability");
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

    private static void assertListEquals(
            List<Integer> expected,
            List<Integer> actual,
            String description) {
        if (!expected.equals(actual)) {
            throw new AssertionError(
                    description + " expected " + expected + " but was " + actual);
        }
    }

    private static void assertEquals(int expected, int actual, String description) {
        if (expected != actual) {
            throw new AssertionError(
                    description + " expected " + expected + " but was " + actual);
        }
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
