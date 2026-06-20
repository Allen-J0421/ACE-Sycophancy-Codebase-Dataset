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
        WeightedGraph graph = BellmanFordFixtures.sampleGraph();

        ShortestPathResult result = BellmanFord.computeShortestPaths(graph, 0);
        assertFalse(result.hasNegativeCycle(), "expected a valid shortest-path result");
        assertArrayEquals(new int[] {0, 5, 6, 6, 7}, result.distances(), "incorrect shortest paths");
    }

    private static void testNegativeCycleDetection() {
        WeightedGraph graph = WeightedGraph.of(
            3,
            WeightedEdge.of(0, 1, 1),
            WeightedEdge.of(1, 2, -1),
            WeightedEdge.of(2, 0, -1)
        );

        ShortestPathResult result = BellmanFord.computeShortestPaths(graph, 0);
        assertTrue(result.hasNegativeCycle(), "expected negative cycle detection");
    }

    private static void testInvalidEdgeValidation() {
        try {
            WeightedGraph.from(2, new int[][] {{0, 2, 3}});
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
