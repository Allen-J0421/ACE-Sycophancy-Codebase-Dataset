import java.util.Arrays;

/**
 * Lightweight, dependency-free tests for {@link BellmanFord} and friends.
 *
 * <p>Run with: {@code javac *.java && java BellmanFordTest}
 */
final class BellmanFordTest {

    private static int passed;
    private static int failed;

    public static void main(String[] args) {
        sampleGraphProducesKnownDistances();
        unreachableVerticesAreReported();
        negativeWeightsAreHandled();
        negativeCycleIsDetected();
        resultTypeDistinguishesTheTwoCases();
        invalidGraphInputIsRejected();
        invalidSourceIsRejected();

        System.out.println(passed + " passed, " + failed + " failed");
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void sampleGraphProducesKnownDistances() {
        WeightedGraph graph = WeightedGraph.from(5, new int[][] {
            {1, 3, 2}, {4, 3, -1}, {2, 4, 1}, {1, 2, 1}, {0, 1, 5}
        });
        Distances distances = succeed(BellmanFord.shortestPaths(graph, 0));

        checkArray("sample distances", new int[] {0, 5, 6, 6, 7}, distances.all());
    }

    private static void unreachableVerticesAreReported() {
        // Vertex 2 is isolated; vertex 1 is reachable from 0.
        WeightedGraph graph = WeightedGraph.of(3, WeightedEdge.of(0, 1, 4));
        Distances distances = succeed(BellmanFord.shortestPaths(graph, 0));

        check("source reachable", distances.isReachable(0));
        check("neighbour reachable", distances.isReachable(1));
        check("isolated vertex unreachable", !distances.isReachable(2));
        check("unreachable distance sentinel",
            distances.distanceTo(2) == Distances.UNREACHABLE);
    }

    private static void negativeWeightsAreHandled() {
        WeightedGraph graph = WeightedGraph.of(3,
            WeightedEdge.of(0, 1, 4),
            WeightedEdge.of(0, 2, 5),
            WeightedEdge.of(1, 2, -3));
        Distances distances = succeed(BellmanFord.shortestPaths(graph, 0));

        check("negative edge improves path", distances.distanceTo(2) == 1);
    }

    private static void negativeCycleIsDetected() {
        WeightedGraph graph = WeightedGraph.of(3,
            WeightedEdge.of(0, 1, 1),
            WeightedEdge.of(1, 2, -1),
            WeightedEdge.of(2, 1, -1)); // 1 -> 2 -> 1 sums to -2
        ShortestPathResult result = BellmanFord.shortestPaths(graph, 0);

        check("negative cycle detected", result instanceof NegativeCycle);
    }

    private static void resultTypeDistinguishesTheTwoCases() {
        WeightedGraph healthy = WeightedGraph.of(2, WeightedEdge.of(0, 1, 1));
        WeightedGraph cyclic = WeightedGraph.of(2,
            WeightedEdge.of(0, 1, 1),
            WeightedEdge.of(1, 0, -2));

        check("healthy graph yields Distances",
            BellmanFord.shortestPaths(healthy, 0) instanceof Distances);
        check("cyclic graph yields NegativeCycle",
            BellmanFord.shortestPaths(cyclic, 0) instanceof NegativeCycle);
    }

    private static void invalidGraphInputIsRejected() {
        checkThrows("out-of-range endpoint rejected", IllegalArgumentException.class,
            () -> WeightedGraph.of(2, WeightedEdge.of(0, 5, 1)));
        checkThrows("non-positive vertex count rejected", IllegalArgumentException.class,
            () -> WeightedGraph.of(0));
    }

    private static void invalidSourceIsRejected() {
        WeightedGraph graph = WeightedGraph.of(2, WeightedEdge.of(0, 1, 1));
        checkThrows("out-of-range source rejected", IllegalArgumentException.class,
            () -> BellmanFord.shortestPaths(graph, 7));
    }

    // --- tiny assertion helpers -------------------------------------------------

    /** Asserts the result is a success and returns the {@link Distances} for inspection. */
    private static Distances succeed(ShortestPathResult result) {
        if (result instanceof Distances distances) {
            return distances;
        }
        failed++;
        System.out.println("FAIL: expected Distances but got " + result);
        throw new AssertionError(result);
    }

    private static void check(String name, boolean condition) {
        if (condition) {
            passed++;
        } else {
            failed++;
            System.out.println("FAIL: " + name);
        }
    }

    private static void checkArray(String name, int[] expected, int[] actual) {
        if (Arrays.equals(expected, actual)) {
            passed++;
        } else {
            failed++;
            System.out.println("FAIL: " + name
                + " expected " + Arrays.toString(expected)
                + " but got " + Arrays.toString(actual));
        }
    }

    private static void checkThrows(String name, Class<? extends Throwable> type, Runnable action) {
        try {
            action.run();
            failed++;
            System.out.println("FAIL: " + name + " (expected " + type.getSimpleName() + ")");
        } catch (Throwable t) {
            check(name, type.isInstance(t));
        }
    }
}
