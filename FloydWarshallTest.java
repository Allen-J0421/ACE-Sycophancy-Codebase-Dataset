import java.util.Arrays;

public final class FloydWarshallTest {
    private static final int INF = FloydWarshall.NO_PATH;
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final int[][] INVALID_RECTANGULAR_MATRIX = {{0}, {1, 0}};
    private static final int[][] MATRIX_WITH_NULL_ROW = {{0}, null};
    private static final int[][] NON_SQUARE_ROW_MATRIX = {{0, 1}};

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
        int[][] graph = acyclicThreeNodeGraph();

        int[][] original = Matrices.copyOf(graph);
        FloydWarshall.shortestPaths(graph);

        assertMatrixEquals(original, graph);
    }

    private static void detectsNegativeCycles() {
        int[][] graph = negativeCycleGraph();

        int[][] distances = FloydWarshall.shortestPaths(graph);
        assertTrue(FloydWarshall.hasNegativeCycle(distances), "Expected a negative cycle.");
    }

    private static void rejectsInvalidMatrices() {
        assertInvalidGraphRejected(null);
        assertInvalidGraphRejected(INVALID_RECTANGULAR_MATRIX);
        assertInvalidGraphRejected(MATRIX_WITH_NULL_ROW);
        assertInvalidGraphRejected(NON_SQUARE_ROW_MATRIX);
    }

    private static void rejectsInvalidFormatterMatrices() {
        assertInvalidDistanceMatrixRejected(null);
        assertInvalidDistanceMatrixRejected(INVALID_RECTANGULAR_MATRIX);
    }

    private static void throwsOnDistanceOverflow() {
        int[][] graph = overflowGraph();

        assertThrows(ArithmeticException.class, () -> FloydWarshall.shortestPaths(graph));
    }

    private static void formatsUnreachableDistances() {
        int[][] distances = unreachableTwoNodeDistances();

        String expected = "0 INF" + LINE_SEPARATOR + "INF 0" + LINE_SEPARATOR;
        assertEquals(expected, DistanceMatrixFormatter.format(distances));
    }

    private static void assertInvalidGraphRejected(int[][] graph) {
        assertThrows(IllegalArgumentException.class, () -> FloydWarshall.shortestPaths(graph));
    }

    private static void assertInvalidDistanceMatrixRejected(int[][] distances) {
        assertThrows(IllegalArgumentException.class, () -> DistanceMatrixFormatter.format(distances));
    }

    private static int[][] acyclicThreeNodeGraph() {
        return new int[][] {
            {0, 2, INF},
            {INF, 0, 3},
            {INF, INF, 0}
        };
    }

    private static int[][] negativeCycleGraph() {
        return new int[][] {
            {0, 1, INF},
            {INF, 0, -2},
            {-2, INF, 0}
        };
    }

    private static int[][] overflowGraph() {
        return new int[][] {
            {0, Integer.MAX_VALUE - 1, INF},
            {INF, 0, 2},
            {INF, INF, 0}
        };
    }

    private static int[][] unreachableTwoNodeDistances() {
        return new int[][] {
            {0, INF},
            {INF, 0}
        };
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
