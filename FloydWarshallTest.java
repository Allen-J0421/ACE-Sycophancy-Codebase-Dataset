import java.util.Arrays;

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
        assertMatrixEquals(
            ExampleGraphs.weightedDirectedGraphShortestPaths(),
            FloydWarshall.shortestPaths(ExampleGraphs.weightedDirectedGraph())
        );
    }

    private static void doesNotMutateInput() {
        int[][] graph = TestGraphs.acyclicThreeNodeGraph();

        int[][] original = Matrices.copyOf(graph);
        FloydWarshall.shortestPaths(graph);

        assertMatrixEquals(original, graph);
    }

    private static void detectsNegativeCycles() {
        int[][] graph = TestGraphs.negativeCycleGraph();

        int[][] distances = FloydWarshall.shortestPaths(graph);
        assertTrue(FloydWarshall.hasNegativeCycle(distances), "Expected a negative cycle.");
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

        assertThrows(ArithmeticException.class, () -> FloydWarshall.shortestPaths(graph));
    }

    private static void formatsUnreachableDistances() {
        int[][] distances = TestGraphs.unreachableTwoNodeDistances();

        String expected = "0 INF" + LINE_SEPARATOR + "INF 0" + LINE_SEPARATOR;
        assertEquals(expected, DistanceMatrixFormatter.format(distances));
    }

    private static void assertInvalidGraphRejected(int[][] graph) {
        assertThrows(IllegalArgumentException.class, () -> FloydWarshall.shortestPaths(graph));
    }

    private static void assertInvalidDistanceMatrixRejected(int[][] distances) {
        assertThrows(IllegalArgumentException.class, () -> DistanceMatrixFormatter.format(distances));
    }

    private static void assertMatrixEquals(int[][] expected, int[][] actual) {
        if (!Arrays.deepEquals(expected, actual)) {
            throw new AssertionError(
                "Expected " + Arrays.deepToString(expected) + " but was " + Arrays.deepToString(actual)
            );
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertEquals(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected \"" + expected + "\" but was \"" + actual + "\"");
        }
    }

    private static void assertThrows(Class<? extends Throwable> expectedType, Runnable action) {
        try {
            action.run();
        } catch (Throwable error) {
            if (expectedType.isInstance(error)) {
                return;
            }
            throw new AssertionError(
                "Expected " + expectedType.getSimpleName() + " but caught " + error.getClass().getSimpleName(),
                error
            );
        }

        throw new AssertionError("Expected " + expectedType.getSimpleName() + " to be thrown.");
    }
}
