public final class BipartiteGraphTest {
    private BipartiteGraphTest() {
        // Utility class.
    }

    public static void main(String[] args) {
        runTestCases();
        runTest("rejects invalid edges", BipartiteGraphTest::shouldRejectInvalidEdges);
        runTest("rejects invalid neighbor access", BipartiteGraphTest::shouldRejectInvalidNeighborAccess);
        runTest("is directly iterable", BipartiteGraphTest::shouldBeDirectlyIterable);
    }

    private static void runTestCases() {
        for (BipartiteCase testCase : bipartiteCases()) {
            runTest(testCase.name, () -> assertBipartiteCase(testCase));
        }
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

    private static void shouldBeDirectlyIterable() {
        Graph graph = Graph.fromEdges(4, new int[][] {{0, 1}});
        assertVertexSequence(graph, new int[] {0, 1, 2, 3});
        assertIteratorExhaustion(graph);
    }

    private static void assertVertexSequence(
            Iterable<Integer> vertices,
            int[] expectedVertices) {
        int index = 0;
        for (int vertex : vertices) {
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

    private static void assertBipartiteCase(BipartiteCase testCase) {
        assertEquals(
                testCase.expectedBipartite,
                BipartiteGraph.isBipartite(testCase.vertexCount, testCase.edges),
                testCase.failureMessage);

        Graph graph = Graph.fromEdges(testCase.vertexCount, testCase.edges);
        assertEquals(
                testCase.expectedBipartite,
                BipartiteGraph.isBipartite(graph),
                testCase.failureMessage + " Graph overload should match.");
    }

    private static void assertIteratorExhaustion(Graph graph) {
        assertThrows(
                java.util.NoSuchElementException.class,
                () -> {
                    java.util.Iterator<Integer> iterator = graph.iterator();
                    while (iterator.hasNext()) {
                        iterator.next();
                    }
                    iterator.next();
                },
                "Expected graph iterators to reject reads past the end.");
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

    private static BipartiteCase[] bipartiteCases() {
        return new BipartiteCase[] {
            new BipartiteCase(
                    "identifies bipartite graphs",
                    true,
                    4,
                    new int[][] {{0, 1}, {0, 3}, {2, 1}, {2, 3}},
                    "Expected an even-cycle graph to be bipartite."),
            new BipartiteCase(
                    "rejects non-bipartite graphs",
                    false,
                    3,
                    new int[][] {{0, 1}, {1, 2}, {2, 0}},
                    "Expected an odd cycle to be non-bipartite."),
            new BipartiteCase(
                    "handles disconnected graphs",
                    true,
                    5,
                    new int[][] {{0, 1}, {2, 3}},
                    "Expected disconnected acyclic components to be bipartite.")
        };
    }

    private static final class BipartiteCase {
        private final String name;
        private final boolean expectedBipartite;
        private final int vertexCount;
        private final int[][] edges;
        private final String failureMessage;

        private BipartiteCase(
                String name,
                boolean expectedBipartite,
                int vertexCount,
                int[][] edges,
                String failureMessage) {
            this.name = name;
            this.expectedBipartite = expectedBipartite;
            this.vertexCount = vertexCount;
            this.edges = edges;
            this.failureMessage = failureMessage;
        }
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Throwable;
    }
}
