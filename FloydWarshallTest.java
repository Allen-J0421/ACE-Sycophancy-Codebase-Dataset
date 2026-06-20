public final class FloydWarshallTest {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private FloydWarshallTest() {
    }

    public static void main(String[] args) {
        computesShortestPaths();
        doesNotMutateInput();
        detectsNegativeCycles();
        rejectsInvalidMatrices();
        rejectsInvalidFormatterMatrices();
        throwsOnDistanceOverflow();
        formatsUnreachableDistances();

        System.out.println("All FloydWarshall tests passed.");
    }

    private static void computesShortestPaths() {
        TestAssertions.assertMatrixEquals(
            ExampleGraphs.weightedDirectedGraphShortestPaths(),
            FloydWarshall.shortestPaths(ExampleGraphs.weightedDirectedGraph())
        );
    }

    private static void doesNotMutateInput() {
        int[][] graph = TestGraphs.acyclicThreeNodeGraph();

        int[][] original = MatrixUtils.copyOf(graph);
        FloydWarshall.shortestPaths(graph);

        TestAssertions.assertMatrixEquals(original, graph);
    }

    private static void detectsNegativeCycles() {
        int[][] graph = TestGraphs.negativeCycleGraph();

        int[][] distances = FloydWarshall.shortestPaths(graph);
        TestAssertions.assertTrue(FloydWarshall.hasNegativeCycle(distances), "Expected a negative cycle.");
    }

    private static void rejectsInvalidMatrices() {
        assertInvalidGraphRejected(null);
        assertInvalidGraphRejected(TestGraphs.invalidRectangularMatrix());
        assertInvalidGraphRejected(TestGraphs.matrixWithNullRow());
        assertInvalidGraphRejected(TestGraphs.nonSquareRowMatrix());
    }

    private static void rejectsInvalidFormatterMatrices() {
        assertInvalidDistanceMatrixRejected(null);
        assertInvalidDistanceMatrixRejected(TestGraphs.invalidRectangularMatrix());
    }

    private static void throwsOnDistanceOverflow() {
        int[][] graph = TestGraphs.overflowGraph();

        TestAssertions.assertThrows(ArithmeticException.class, () -> FloydWarshall.shortestPaths(graph));
    }

    private static void formatsUnreachableDistances() {
        int[][] distances = TestGraphs.unreachableTwoNodeDistances();

        String expected = "0 INF" + LINE_SEPARATOR + "INF 0" + LINE_SEPARATOR;
        TestAssertions.assertEquals(expected, DistanceMatrixFormatter.format(distances));
    }

    private static void assertInvalidGraphRejected(int[][] graph) {
        TestAssertions.assertThrows(IllegalArgumentException.class, () -> FloydWarshall.shortestPaths(graph));
    }

    private static void assertInvalidDistanceMatrixRejected(int[][] distances) {
        TestAssertions.assertThrows(IllegalArgumentException.class, () -> DistanceMatrixFormatter.format(distances));
    }
}
