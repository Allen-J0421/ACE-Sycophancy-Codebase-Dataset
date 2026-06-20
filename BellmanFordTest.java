import java.util.Arrays;

final class BellmanFordTest {
    private BellmanFordTest() {
    }

    public static void main(String[] args) {
        testShortestPaths();
        testLegacyShortestPaths();
        testRawTypedShortestPaths();
        testUnreachableVertexHandling();
        testNegativeCycleDetection();
        testLegacyNegativeCycleDetection();
        testInvalidEdgeValidation();
        testGraphEdgesAreImmutable();
        System.out.println("All BellmanFord tests passed.");
    }

    private static void testShortestPaths() {
        BellmanFordFixtures.Case sampleCase = BellmanFordFixtures.sampleCase();

        ShortestPathResult result = BellmanFord.computeShortestPaths(
            sampleCase.graph(),
            sampleCase.source()
        );
        assertFalse(result.hasNegativeCycle(), "expected a valid shortest-path result");
        assertArrayEquals(sampleCase.expectedDistances(), result.distances(), "incorrect shortest paths");
        assertEquals(6, result.distanceTo(3), "incorrect distance lookup");
        assertEquals(5, result.vertexCount(), "incorrect vertex count");
    }

    private static void testLegacyShortestPaths() {
        BellmanFordFixtures.Case sampleCase = BellmanFordFixtures.sampleCase();
        int[] distances = BellmanFord.shortestPaths(
            sampleCase.graph().vertices(),
            sampleCase.edgeData(),
            sampleCase.source()
        );
        assertArrayEquals(sampleCase.expectedDistances(), distances, "incorrect legacy shortest paths");
    }

    private static void testRawTypedShortestPaths() {
        BellmanFordFixtures.Case sampleCase = BellmanFordFixtures.sampleCase();
        ShortestPathResult result = BellmanFord.computeShortestPaths(
            sampleCase.graph().vertices(),
            sampleCase.edgeData(),
            sampleCase.source()
        );
        assertArrayEquals(sampleCase.expectedDistances(), result.distances(), "incorrect raw typed shortest paths");
    }

    private static void testNegativeCycleDetection() {
        BellmanFordFixtures.Case negativeCycleCase = BellmanFordFixtures.negativeCycleCase();
        ShortestPathResult result = BellmanFord.computeShortestPaths(
            negativeCycleCase.graph(),
            negativeCycleCase.source()
        );
        assertTrue(result.hasNegativeCycle(), "expected negative cycle detection");
    }

    private static void testUnreachableVertexHandling() {
        BellmanFordFixtures.Case unreachableVertexCase = BellmanFordFixtures.unreachableVertexCase();
        ShortestPathResult result = BellmanFord.computeShortestPaths(
            unreachableVertexCase.graph(),
            unreachableVertexCase.source()
        );

        assertFalse(result.hasNegativeCycle(), "unreachable vertices should not imply a negative cycle");
        assertFalse(result.isReachable(3), "vertex 3 should be unreachable");
        assertArrayEquals(
            unreachableVertexCase.expectedDistances(),
            result.distances(),
            "legacy distance view should preserve the unreachable sentinel"
        );

        try {
            result.distanceTo(3);
            throw new AssertionError("expected unreachable distance lookup to fail");
        } catch (IllegalStateException expected) {
            assertTrue(expected.getMessage().contains("unreachable"), "unexpected unreachable message");
        }
    }

    private static void testLegacyNegativeCycleDetection() {
        BellmanFordFixtures.Case negativeCycleCase = BellmanFordFixtures.negativeCycleCase();
        int[] result = BellmanFord.shortestPaths(
            negativeCycleCase.graph().vertices(),
            negativeCycleCase.edgeData(),
            negativeCycleCase.source()
        );
        assertArrayEquals(
            negativeCycleCase.expectedDistances(),
            result,
            "legacy API should return the negative-cycle sentinel"
        );
    }

    private static void testInvalidEdgeValidation() {
        try {
            WeightedGraph.from(2, new int[][] {{0, 2, 3}});
            throw new AssertionError("expected invalid edge validation to fail");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("edge end"), "unexpected validation message");
        }
    }

    private static void testGraphEdgesAreImmutable() {
        WeightedGraph graph = BellmanFordFixtures.sampleCase().graph();
        try {
            graph.edges().add(WeightedEdge.of(3, 4, 1));
            throw new AssertionError("expected graph edges view to be immutable");
        } catch (UnsupportedOperationException expected) {
            assertTrue(true, "immutable list should reject modifications");
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

    private static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + ": expected " + expected + " but was " + actual);
        }
    }
}
