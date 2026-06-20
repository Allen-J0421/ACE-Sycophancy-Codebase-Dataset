import java.util.List;
import java.util.stream.Collectors;

final class ConnectedComponentsTest {

    private ConnectedComponentsTest() {
    }

    public static void main(String[] args) {
        verifiesEmptyGraph();
        verifiesConnectedComponentsForSampleGraph();
        verifiesSingleVertexComponents();
        verifiesFormattedOutput();
        verifiesResultIsImmutable();
        verifiesInvalidNeighborGraphIsRejected();
        verifiesInvalidVertexListGraphIsRejected();
    }

    private static void verifiesEmptyGraph() {
        ConnectedComponentsResult result =
                GraphComponentFinder.findConnectedComponents(UndirectedGraph.empty(0));

        assertTrue(result.isEmpty(), "empty graph should produce no components");
        assertEquals(0, result.componentCount(), "empty graph component count");
        assertEquals("", result.format(), "empty graph formatted output");
    }

    private static void verifiesConnectedComponentsForSampleGraph() {
        ConnectedComponentsResult result =
                GraphComponentFinder.findConnectedComponents(GraphExamples.createSampleGraph());

        assertFalse(result.isEmpty(), "sample graph should produce components");
        assertEquals(2, result.componentCount(), "sample graph component count");
        assertNestedVertexIndexesEqual(
                List.of(List.of(0, 3, 2, 1), List.of(4, 5)),
                toVertexIndexes(result),
                "sample graph components");
    }

    private static void verifiesSingleVertexComponents() {
        UndirectedGraph graph = UndirectedGraph.empty(3);

        ConnectedComponentsResult result = GraphComponentFinder.findConnectedComponents(graph);

        assertFalse(result.isEmpty(), "isolated graph should produce components");
        assertEquals(3, result.componentCount(), "isolated vertex component count");
        assertNestedVertexIndexesEqual(
                List.of(List.of(0), List.of(1), List.of(2)),
                toVertexIndexes(result),
                "isolated vertex components");
    }

    private static void verifiesFormattedOutput() {
        ConnectedComponentsResult result =
                GraphComponentFinder.findConnectedComponents(GraphExamples.createSampleGraph());

        ConnectedComponent component = result.componentAt(0);
        assertFalse(component.isEmpty(), "sample component should not be empty");
        assertEquals(0, component.vertexAt(0).index(), "first sample component vertex");
        assertEquals("0 3 2 1" + System.lineSeparator() + "4 5",
                result.format(),
                "formatted output");
    }

    private static void verifiesResultIsImmutable() {
        ConnectedComponentsResult result =
                GraphComponentFinder.findConnectedComponents(GraphExamples.createSampleGraph());

        assertThrows(UnsupportedOperationException.class,
                () -> removeFirstComponent(result),
                "result iterator immutability");
        assertThrows(UnsupportedOperationException.class,
                () -> removeFirstVertex(result.componentAt(0)),
                "component iterator immutability");
    }

    private static void verifiesInvalidNeighborGraphIsRejected() {
        Graph invalidGraph = new Graph() {
            @Override
            public int vertexCount() {
                return 1;
            }

            @Override
            public List<Vertex> vertices() {
                return List.of(new Vertex(0));
            }

            @Override
            public List<Vertex> neighborsOf(Vertex vertex) {
                return List.of(new Vertex(2));
            }
        };

        assertThrows(IllegalArgumentException.class,
                () -> GraphComponentFinder.findConnectedComponents(invalidGraph),
                "invalid neighbor graph");
    }

    private static void verifiesInvalidVertexListGraphIsRejected() {
        Graph invalidGraph = new Graph() {
            @Override
            public int vertexCount() {
                return 2;
            }

            @Override
            public List<Vertex> vertices() {
                return List.of(new Vertex(0), new Vertex(0));
            }

            @Override
            public List<Vertex> neighborsOf(Vertex vertex) {
                return List.of();
            }
        };

        assertThrows(IllegalArgumentException.class,
                () -> GraphComponentFinder.findConnectedComponents(invalidGraph),
                "invalid vertex list graph");
    }

    private static List<List<Integer>> toVertexIndexes(ConnectedComponentsResult result) {
        return resultToComponentIndexes(result);
    }

    private static List<List<Integer>> resultToComponentIndexes(ConnectedComponentsResult result) {
        return stream(result)
                .map(ConnectedComponentsTest::componentToVertexIndexes)
                .collect(Collectors.toList());
    }

    private static List<Integer> componentToVertexIndexes(ConnectedComponent component) {
        return stream(component)
                .map(Vertex::index)
                .collect(Collectors.toList());
    }

    private static <T> java.util.stream.Stream<T> stream(Iterable<T> values) {
        return java.util.stream.StreamSupport.stream(values.spliterator(), false);
    }

    private static void removeFirstComponent(ConnectedComponentsResult result) {
        java.util.Iterator<ConnectedComponent> iterator = result.iterator();
        iterator.next();
        iterator.remove();
    }

    private static void removeFirstVertex(ConnectedComponent component) {
        java.util.Iterator<Vertex> iterator = component.iterator();
        iterator.next();
        iterator.remove();
    }

    private static void assertNestedVertexIndexesEqual(
            List<List<Integer>> expected,
            List<List<Integer>> actual,
            String description) {
        if (!expected.equals(actual)) {
            throw new AssertionError(
                    description + " expected " + expected + " but was " + actual);
        }
    }

    private static void assertFalse(boolean condition, String description) {
        if (condition) {
            throw new AssertionError(description);
        }
    }

    private static void assertTrue(boolean condition, String description) {
        if (!condition) {
            throw new AssertionError(description);
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
