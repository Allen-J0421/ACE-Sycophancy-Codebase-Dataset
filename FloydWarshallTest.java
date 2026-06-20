import java.util.Arrays;

public final class FloydWarshallTest {
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
                FloydWarshall.shortestPaths(ExampleGraphs.weightedDirectedGraph()));
    }

    private static void doesNotMutateInput() {
        int[][] graph = {
                {0, 2, FloydWarshall.INF},
                {FloydWarshall.INF, 0, 3},
                {FloydWarshall.INF, FloydWarshall.INF, 0}
        };
        int[][] original = copyMatrix(graph);

        FloydWarshall.shortestPaths(graph);

        assertMatrixEquals(original, graph);
    }

    private static void detectsNegativeCycles() {
        int[][] graph = {
                {0, 1, FloydWarshall.INF},
                {FloydWarshall.INF, 0, -2},
                {-2, FloydWarshall.INF, 0}
        };

        int[][] distances = FloydWarshall.shortestPaths(graph);

        assertTrue(FloydWarshall.hasNegativeCycle(distances), "Expected a negative cycle.");
    }

    private static void rejectsInvalidMatrices() {
        assertThrows(
                IllegalArgumentException.class,
                () -> FloydWarshall.shortestPaths(null));
        assertThrows(
                IllegalArgumentException.class,
                () -> FloydWarshall.shortestPaths(new int[][]{{0}, {1, 0}}));
        assertThrows(
                IllegalArgumentException.class,
                () -> FloydWarshall.shortestPaths(new int[][]{{0}, null}));
        assertThrows(
                IllegalArgumentException.class,
                () -> FloydWarshall.shortestPaths(new int[][]{{0, 1}}));
    }

    private static void rejectsInvalidFormatterMatrices() {
        assertThrows(
                IllegalArgumentException.class,
                () -> DistanceMatrixFormatter.format(null));
        assertThrows(
                IllegalArgumentException.class,
                () -> DistanceMatrixFormatter.format(new int[][]{{0}, {1, 0}}));
    }

    private static void throwsOnDistanceOverflow() {
        int[][] graph = {
                {0, Integer.MAX_VALUE - 1, FloydWarshall.INF},
                {FloydWarshall.INF, 0, 2},
                {FloydWarshall.INF, FloydWarshall.INF, 0}
        };

        assertThrows(
                ArithmeticException.class,
                () -> FloydWarshall.shortestPaths(graph));
    }

    private static void formatsUnreachableDistances() {
        int[][] distances = {
                {0, FloydWarshall.INF},
                {FloydWarshall.INF, 0}
        };
        String lineSeparator = System.lineSeparator();
        String expected = "0 INF" + lineSeparator + "INF 0" + lineSeparator;

        assertEquals(expected, DistanceMatrixFormatter.format(distances));
    }

    private static int[][] copyMatrix(int[][] matrix) {
        int[][] copy = new int[matrix.length][];
        for (int row = 0; row < matrix.length; row++) {
            copy[row] = Arrays.copyOf(matrix[row], matrix[row].length);
        }
        return copy;
    }

    private static void assertMatrixEquals(int[][] expected, int[][] actual) {
        if (!Arrays.deepEquals(expected, actual)) {
            throw new AssertionError("Expected " + Arrays.deepToString(expected)
                    + " but got " + Arrays.deepToString(actual));
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertEquals(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected \"" + expected + "\" but got \"" + actual + "\".");
        }
    }

    private static void assertThrows(Class<? extends Throwable> expectedType, Runnable runnable) {
        try {
            runnable.run();
        } catch (Throwable throwable) {
            if (expectedType.isInstance(throwable)) {
                return;
            }

            throw new AssertionError("Expected " + expectedType.getSimpleName()
                    + " but got " + throwable.getClass().getSimpleName() + ".", throwable);
        }

        throw new AssertionError("Expected " + expectedType.getSimpleName() + ".");
    }
}
