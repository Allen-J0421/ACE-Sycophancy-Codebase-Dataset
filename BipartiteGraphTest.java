public final class BipartiteGraphTest {
    private BipartiteGraphTest() {
        // Utility class.
    }

    public static void main(String[] args) {
        runTest("identifies bipartite graphs", BipartiteGraphTest::shouldIdentifyABipartiteGraph);
        runTest("rejects non-bipartite graphs", BipartiteGraphTest::shouldRejectANonBipartiteGraph);
        runTest("handles disconnected graphs", BipartiteGraphTest::shouldHandleDisconnectedGraphs);
        runTest("rejects invalid edges", BipartiteGraphTest::shouldRejectInvalidEdges);
        runTest("rejects invalid neighbor access", BipartiteGraphTest::shouldRejectInvalidNeighborAccess);
        runTest("exposes stable vertex iteration", BipartiteGraphTest::shouldExposeStableVertexIteration);
    }

    private static void shouldIdentifyABipartiteGraph() {
        assertBipartiteResult(
                true,
                4,
                new int[][] {{0, 1}, {0, 3}, {2, 1}, {2, 3}},
                "Expected an even-cycle graph to be bipartite.");
    }

    private static void shouldRejectANonBipartiteGraph() {
        assertBipartiteResult(
                false,
                3,
                new int[][] {{0, 1}, {1, 2}, {2, 0}},
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

    private static void shouldExposeStableVertexIteration() {
        Graph graph = Graph.fromEdges(3, new int[][] {{0, 1}});
        assertVertexSequence(graph, new int[] {0, 1, 2});
        assertThrows(
                UnsupportedOperationException.class,
                () -> graph.vertices().add(3),
                "Expected vertex iteration to be immutable.");
    }

    private static void assertBipartiteResult(
            boolean expected,
            int vertexCount,
            int[][] edges,
            String message) {
        assertEquals(expected, BipartiteGraph.isBipartite(vertexCount, edges), message);
    }

    private static void assertVertexSequence(Graph graph, int[] expectedVertices) {
        int index = 0;
        for (int vertex : graph.vertices()) {
            if (index >= expectedVertices.length || vertex != expectedVertices[index]) {
                throw new AssertionError("Unexpected vertex iteration order.");
            }

            index++;
        }

        if (index != expectedVertices.length) {
            throw new AssertionError("Vertex iteration did not include all expected vertices.");
        }
    }

    private static void assertEquals(boolean expected, boolean actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + " Expected: " + expected + ", actual: " + actual);
        }
    }

    private static void runTest(String name, ThrowingRunnable test) {
        try {
            test.run();
        } catch (Throwable throwable) {
            throw new AssertionError("Test failed: " + name, throwable);
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
