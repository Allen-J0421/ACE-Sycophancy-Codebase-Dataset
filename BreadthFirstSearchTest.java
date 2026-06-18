import java.util.Arrays;
import java.util.List;

public final class BreadthFirstSearchTest {

    private BreadthFirstSearchTest() {
        // Test utility class.
    }

    public static void main(String[] args) {
        testBfsOverDisconnectedGraph();
        testBfsFromSource();
        testNeighborsAreReadOnly();
        testDirectedEdges();
        testInvalidInputs();
        System.out.println("All tests passed.");
    }

    private static void testBfsOverDisconnectedGraph() {
        Graph graph = sampleGraph();
        assertEquals(Arrays.asList(0, 2, 3, 1, 4, 5), BreadthFirstSearch.bfs(graph), "bfs traversal");
    }

    private static void testBfsFromSource() {
        Graph graph = sampleGraph();
        assertEquals(Arrays.asList(1, 2, 0, 3), BreadthFirstSearch.bfsFromSource(graph, 1), "bfsFromSource traversal");
    }

    private static void testInvalidInputs() {
        expectThrows(IllegalArgumentException.class, () -> Graph.create(-1));

        Graph graph = Graph.create(2);
        expectThrows(IllegalArgumentException.class, () -> graph.addUndirectedEdge(0, 2));
        expectThrows(IllegalArgumentException.class, () -> graph.neighbors(2));
        expectThrows(IllegalArgumentException.class, () -> BreadthFirstSearch.bfsFromSource(graph, -1));
        expectThrows(NullPointerException.class, () -> BreadthFirstSearch.bfs(null));
    }

    private static void testDirectedEdges() {
        Graph graph = Graph.create(3);
        graph.addDirectedEdge(0, 1);
        graph.addDirectedEdge(1, 2);

        assertEquals(Arrays.asList(1), graph.neighbors(0), "directed edge from 0");
        assertEquals(Arrays.asList(2), graph.neighbors(1), "directed edge from 1");
        assertEquals(List.of(), graph.neighbors(2), "directed edge from 2");
        assertEquals(Arrays.asList(0, 1, 2), BreadthFirstSearch.bfs(graph), "directed bfs traversal");
    }

    private static void testNeighborsAreReadOnly() {
        Graph graph = Graph.create(2);
        graph.addUndirectedEdge(0, 1);

        List<Integer> neighbors = graph.neighbors(0);
        if (!neighbors.equals(Arrays.asList(1))) {
            throw new AssertionError("neighbors expected [1] but was " + neighbors);
        }
        expectThrows(UnsupportedOperationException.class, () -> neighbors.add(2));
    }

    private static Graph sampleGraph() {
        Graph graph = Graph.create(6);
        graph.addUndirectedEdge(1, 2);
        graph.addUndirectedEdge(2, 0);
        graph.addUndirectedEdge(0, 3);
        graph.addUndirectedEdge(4, 5);
        return graph;
    }

    private static void assertEquals(List<Integer> expected, List<Integer> actual, String label) {
        if (!expected.equals(actual)) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }

    private static <T extends Throwable> void expectThrows(Class<T> expectedType, Runnable action) {
        try {
            action.run();
            throw new AssertionError("expected " + expectedType.getSimpleName());
        } catch (Throwable thrown) {
            if (!expectedType.isInstance(thrown)) {
                throw new AssertionError("expected " + expectedType.getSimpleName() + " but was " + thrown.getClass().getSimpleName(), thrown);
            }
        }
    }
}
