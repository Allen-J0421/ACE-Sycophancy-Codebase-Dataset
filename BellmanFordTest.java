import java.util.Arrays;

final class BellmanFordTest {
    private BellmanFordTest() {
    }

    public static void main(String[] args) {
        testShortestPaths();
        testNegativeCycleDetection();
        testInvalidEdgeValidation();
        System.out.println("All BellmanFord tests passed.");
    }

    private static void testShortestPaths() {
        BellmanFord.Graph graph = BellmanFord.Graph.of(
            5,
            BellmanFord.Edge.of(1, 3, 2),
            BellmanFord.Edge.of(4, 3, -1),
            BellmanFord.Edge.of(2, 4, 1),
            BellmanFord.Edge.of(1, 2, 1),
            BellmanFord.Edge.of(0, 1, 5)
        );

        BellmanFord.Result result = BellmanFord.computeShortestPaths(graph, 0);
        assertFalse(result.hasNegativeCycle(), "expected a valid shortest-path result");
        assertArrayEquals(new int[] {0, 5, 6, 6, 7}, result.distances(), "incorrect shortest paths");
    }

    private static void testNegativeCycleDetection() {
        BellmanFord.Graph graph = BellmanFord.Graph.of(
            3,
            BellmanFord.Edge.of(0, 1, 1),
            BellmanFord.Edge.of(1, 2, -1),
            BellmanFord.Edge.of(2, 0, -1)
        );

        BellmanFord.Result result = BellmanFord.computeShortestPaths(graph, 0);
        assertTrue(result.hasNegativeCycle(), "expected negative cycle detection");
    }

    private static void testInvalidEdgeValidation() {
        try {
            BellmanFord.Graph.from(2, new int[][] {{0, 2, 3}});
            throw new AssertionError("expected invalid edge validation to fail");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("edge end"), "unexpected validation message");
        }
    }

    private static void assertArrayEquals(int[] expected, int[] actual, String message) {
        if (!Arrays.equals(expected, actual)) {
            throw new AssertionError(message + ": expected " + Arrays.toString(expected)
                    + " but was " + Arrays.toString(actual));
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertFalse(boolean condition, String message) {
        assertTrue(!condition, message);
    }
}
