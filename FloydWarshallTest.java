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
            FloydWarshall.shortestPaths(ExampleGraphs.weightedDirectedGraph())
        );
    }

    private static void doesNotMutateInput() {
        int inf = FloydWarshall.NO_PATH;
        int[][] graph = {
            {0, 2, inf},
            {inf, 0, 3},
            {inf, inf, 0}
        };

        int[][] original = Matrices.copyOf(graph);
        FloydWarshall.shortestPaths(graph);

        assertMatrixEquals(original, graph);
    }

    private static void detectsNegativeCycles() {
        int inf = FloydWarshall.NO_PATH;
        int[][] graph = {
            {0, 1, inf},
            {inf, 0, -2},
            {-2, inf, 0}
        };

        int[][] distances = FloydWarshall.shortestPaths(graph);
        assertTrue(FloydWarshall.hasNegativeCycle(distances), "Expected a negative cycle.");
    }

    private static void rejectsInvalidMatrices() {
        assertThrows(IllegalArgumentException.class, () -> FloydWarshall.shortestPaths(null));
        assertThrows(
            IllegalArgumentException.class,
            () -> FloydWarshall.shortestPaths(new int[][] {{0}, {1, 0}})
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> FloydWarshall.shortestPaths(new int[][] {{0}, null})
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> FloydWarshall.shortestPaths(new int[][] {{0, 1}})
        );
    }

    private static void rejectsInvalidFormatterMatrices() {
        assertThrows(IllegalArgumentException.class, () -> DistanceMatrixFormatter.format(null));
        assertThrows(
            IllegalArgumentException.class,
            () -> DistanceMatrixFormatter.format(new int[][] {{0}, {1, 0}})
        );
    }

    private static void throwsOnDistanceOverflow() {
        int inf = FloydWarshall.NO_PATH;
        int[][] graph = {
            {0, Integer.MAX_VALUE - 1, inf},
            {inf, 0, 2},
            {inf, inf, 0}
        };

        assertThrows(ArithmeticException.class, () -> FloydWarshall.shortestPaths(graph));
    }

    private static void formatsUnreachableDistances() {
        int inf = FloydWarshall.NO_PATH;
        int[][] distances = {
            {0, inf},
            {inf, 0}
        };

        String lineSeparator = System.lineSeparator();
        String expected = "0 INF" + lineSeparator + "INF 0" + lineSeparator;
        assertEquals(expected, DistanceMatrixFormatter.format(distances));
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
