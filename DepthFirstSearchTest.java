import java.util.List;

public final class DepthFirstSearchTest {

    private DepthFirstSearchTest() {
        // Utility class.
    }

    public static void main(String[] args) {
        shouldTraverseEveryComponent();
        shouldTraverseSingleComponent();
        shouldRejectMalformedEdgeList();
    }

    private static void shouldTraverseEveryComponent() {
        Graph graph = Graph.fromUndirectedEdges(
                6,
                new int[][] {
                    {1, 2},
                    {0, 3},
                    {2, 0},
                    {5, 4}
                });

        assertEquals(List.of(0, 3, 2, 1, 4, 5), DepthFirstSearch.traverse(graph));
    }

    private static void shouldTraverseSingleComponent() {
        Graph graph = Graph.fromUndirectedEdges(
                6,
                new int[][] {
                    {1, 2},
                    {0, 3},
                    {2, 0},
                    {5, 4}
                });

        assertEquals(List.of(5, 4), DepthFirstSearch.traverseFrom(graph, 5));
    }

    private static void shouldRejectMalformedEdgeList() {
        try {
            Graph.fromUndirectedEdges(3, new int[][] {{0, 1, 2}});
            throw new AssertionError("Expected malformed edge list to fail");
        } catch (IllegalArgumentException expected) {
            // Expected.
        }
    }

    private static void assertEquals(List<Integer> expected, List<Integer> actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected " + expected + " but got " + actual);
        }
    }
}
