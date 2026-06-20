public final class BipartiteGraphTest {
    private BipartiteGraphTest() {
        // Utility class.
    }

    public static void main(String[] args) {
        shouldIdentifyABipartiteGraph();
        shouldRejectANonBipartiteGraph();
        shouldHandleDisconnectedGraphs();
        shouldRejectInvalidEdges();
        shouldRejectInvalidNeighborAccess();
    }

    private static void shouldIdentifyABipartiteGraph() {
        assertEquals(
                true,
                BipartiteGraph.isBipartite(
                        4,
                        new int[][] {{0, 1}, {0, 3}, {2, 1}, {2, 3}}),
                "Expected an even-cycle graph to be bipartite.");
    }

    private static void shouldRejectANonBipartiteGraph() {
        assertEquals(
                false,
                BipartiteGraph.isBipartite(
                        3,
                        new int[][] {{0, 1}, {1, 2}, {2, 0}}),
                "Expected an odd cycle to be non-bipartite.");
    }

    private static void shouldHandleDisconnectedGraphs() {
        Graph graph = Graph.fromEdges(5, new int[][] {{0, 1}, {2, 3}});
        assertEquals(
                true,
                BipartiteGraph.isBipartite(graph),
                "Expected disconnected acyclic components to be bipartite.");
    }

    private static void shouldRejectInvalidEdges() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Graph.fromEdges(3, new int[][] {{0, 1}, {1, 3}}),
                "Expected graph construction to reject out-of-range vertices.");
    }

    private static void shouldRejectInvalidNeighborAccess() {
        Graph graph = Graph.fromEdges(2, new int[][] {{0, 1}});
        assertThrows(
                IndexOutOfBoundsException.class,
                () -> graph.neighborsOf(2),
                "Expected neighbor lookup to validate vertex bounds.");
    }

    private static void assertEquals(boolean expected, boolean actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + " Expected: " + expected + ", actual: " + actual);
        }
    }

    private static void assertThrows(
            Class<? extends Throwable> expectedType,
            ThrowingRunnable action,
            String message) {
        try {
            action.run();
        } catch (Throwable throwable) {
            if (expectedType.isInstance(throwable)) {
                return;
            }

            throw new AssertionError(
                    message
                            + " Expected exception: "
                            + expectedType.getSimpleName()
                            + ", actual: "
                            + throwable.getClass().getSimpleName(),
                    throwable);
        }

        throw new AssertionError(
                message + " Expected exception: " + expectedType.getSimpleName() + ".");
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Throwable;
    }
}
