import java.util.Arrays;
import java.util.List;

public final class BreadthFirstSearchTest {

    private BreadthFirstSearchTest() {
        // Test utility class.
    }

    public static void main(String[] args) {
        testBfsOverDisconnectedGraph();
        testBfsFromSource();
        testInvalidInputs();
        System.out.println("All tests passed.");
    }

    private static void testBfsOverDisconnectedGraph() {
        BreadthFirstSearch.Graph graph = BreadthFirstSearch.Graph.create(6);
        graph.addUndirectedEdge(1, 2);
        graph.addUndirectedEdge(2, 0);
        graph.addUndirectedEdge(0, 3);
        graph.addUndirectedEdge(4, 5);

        assertEquals(Arrays.asList(0, 2, 3, 1, 4, 5), BreadthFirstSearch.bfs(graph), "bfs traversal");
    }

    private static void testBfsFromSource() {
        BreadthFirstSearch.Graph graph = BreadthFirstSearch.Graph.create(6);
        graph.addUndirectedEdge(1, 2);
        graph.addUndirectedEdge(2, 0);
        graph.addUndirectedEdge(0, 3);
        graph.addUndirectedEdge(4, 5);

        assertEquals(Arrays.asList(1, 2, 0, 3), BreadthFirstSearch.bfsFromSource(graph, 1), "bfsFromSource traversal");
    }

    private static void testInvalidInputs() {
        expectIllegalArgument(() -> BreadthFirstSearch.Graph.create(-1));

        BreadthFirstSearch.Graph graph = BreadthFirstSearch.Graph.create(2);
        expectIllegalArgument(() -> graph.addUndirectedEdge(0, 2));
        expectIllegalArgument(() -> BreadthFirstSearch.bfsFromSource(graph, -1));
        expectNullPointer(() -> BreadthFirstSearch.bfs(null));
    }

    private static void assertEquals(List<Integer> expected, List<Integer> actual, String label) {
        if (!expected.equals(actual)) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }

    private static void expectIllegalArgument(Runnable action) {
        try {
            action.run();
            throw new AssertionError("expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // Expected.
        }
    }

    private static void expectNullPointer(Runnable action) {
        try {
            action.run();
            throw new AssertionError("expected NullPointerException");
        } catch (NullPointerException expected) {
            // Expected.
        }
    }
}
