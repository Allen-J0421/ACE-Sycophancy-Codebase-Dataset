import java.util.Arrays;

public final class FloydWarshallTest {
    private FloydWarshallTest() {
    }

    public static void main(String[] args) {
        computesShortestPaths();
        doesNotMutateInput();
        detectsNegativeCycles();
        rejectsInvalidMatrices();

        System.out.println("All FloydWarshall tests passed.");
    }

    private static void computesShortestPaths() {
        int[][] graph = {
                {0, 4, FloydWarshall.INF, 5, FloydWarshall.INF},
                {FloydWarshall.INF, 0, 1, FloydWarshall.INF, 6},
                {2, FloydWarshall.INF, 0, 3, FloydWarshall.INF},
                {FloydWarshall.INF, FloydWarshall.INF, 1, 0, 2},
                {1, FloydWarshall.INF, FloydWarshall.INF, 4, 0}
        };

        int[][] expected = {
                {0, 4, 5, 5, 7},
                {3, 0, 1, 4, 6},
                {2, 6, 0, 3, 5},
                {3, 7, 1, 0, 2},
                {1, 5, 5, 4, 0}
        };

        assertMatrixEquals(expected, FloydWarshall.shortestPaths(graph));
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
        assertThrows(() -> FloydWarshall.shortestPaths(null));
        assertThrows(() -> FloydWarshall.shortestPaths(new int[][]{{0}, {1, 0}}));
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

    private static void assertThrows(Runnable runnable) {
        try {
            runnable.run();
        } catch (IllegalArgumentException expected) {
            return;
        }

        throw new AssertionError("Expected IllegalArgumentException.");
    }
}
