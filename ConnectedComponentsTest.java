import java.util.List;

final class ConnectedComponentsTest {

    private ConnectedComponentsTest() {
    }

    public static void main(String[] args) {
        verifiesConnectedComponentsForSampleGraph();
        verifiesSingleVertexComponents();
    }

    private static void verifiesConnectedComponentsForSampleGraph() {
        List<List<Integer>> components =
                GraphComponentFinder.findConnectedComponents(GraphExamples.createSampleGraph());

        assertEquals("[[0, 3, 2, 1], [4, 5]]", components.toString(), "sample graph components");
    }

    private static void verifiesSingleVertexComponents() {
        UndirectedGraph graph = new UndirectedGraph(3);

        List<List<Integer>> components = GraphComponentFinder.findConnectedComponents(graph);

        assertEquals("[[0], [1], [2]]", components.toString(), "isolated vertex components");
    }

    private static void assertEquals(String expected, String actual, String description) {
        if (!expected.equals(actual)) {
            throw new AssertionError(
                    description + " expected " + expected + " but was " + actual);
        }
    }
}
