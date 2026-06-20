import java.util.Arrays;

final class BellmanFordTest {
    private BellmanFordTest() {
    }

    public static void main(String[] args) {
        testShortestPaths();
        testLegacyShortestPaths();
        testNegativeCycleDetection();
        testLegacyNegativeCycleDetection();
        testInvalidEdgeValidation();
        System.out.println("All BellmanFord tests passed.");
    }

    private static void testShortestPaths() {
        WeightedGraph graph = BellmanFordFixtures.sampleGraph();

        ShortestPathResult result = BellmanFord.computeShortestPaths(graph, 0);
        assertFalse(result.hasNegativeCycle(), "expected a valid shortest-path result");
        assertArrayEquals(new int[] {0, 5, 6, 6, 7}, result.distances(), "incorrect shortest paths");
    }

    private static void testLegacyShortestPaths() {
        int[] distances = BellmanFord.shortestPaths(5, BellmanFordFixtures.sampleEdgeData(), 0);
        assertArrayEquals(new int[] {0, 5, 6, 6, 7}, distances, "incorrect legacy shortest paths");
    }

    private static void testNegativeCycleDetection() {
        WeightedGraph graph = BellmanFordFixtures.negativeCycleGraph();
        ShortestPathResult result = BellmanFord.computeShortestPaths(graph, 0);
        assertTrue(result.hasNegativeCycle(), "expected negative cycle detection");
    }

    private static void testLegacyNegativeCycleDetection() {
        int[] result = BellmanFord.shortestPaths(
            3,
            new int[][] {
                {0, 1, 1},
                {1, 2, -1},
                {2, 0, -1}
            },
            0
        );
        assertArrayEquals(new int[] {-1}, result, "legacy API should return the negative-cycle sentinel");
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
