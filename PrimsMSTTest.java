import java.util.List;

class PrimsMSTTest {

    public static void main(String[] args) {
        testBasicMST();
        testSpecificEdges();
        testDifferentStartVertex();
        testSingleVertex();
        testTwoVertexGraph();
        testTriangleGraph();
        testDisconnectedGraph();
        testDisconnectedFromIsolatedVertex();
        testNullGraph();
        testInvalidStartVertex();
        testGraphValidationNonSquare();
        testGraphValidationSelfLoop();
        testGraphValidationNonSymmetric();
        testMstResultEquality();
        testEdgesFromOutOfBounds();
        System.out.println("All tests passed.");
    }

    private static void testBasicMST() {
        MstResult result = new PrimsMST().computeMST(sampleGraph());
        assertEqual(4, result.edges().size(), "edge count");
        assertEqual(16, result.totalWeight(), "total weight");
    }

    private static void testSpecificEdges() {
        // For this graph with startVertex=0, Prim's deterministically produces:
        // 0→1 (weight 2), 1→2 (weight 3), 0→3 (weight 6), 1→4 (weight 5)
        List<Edge> edges = new PrimsMST().computeMST(sampleGraph()).edges();
        assertContainsEdge(edges, 0, 1, 2);
        assertContainsEdge(edges, 1, 2, 3);
        assertContainsEdge(edges, 0, 3, 6);
        assertContainsEdge(edges, 1, 4, 5);
    }

    private static void testDifferentStartVertex() {
        MstAlgorithm algo = new PrimsMST();
        int w0 = algo.computeMST(sampleGraph(), 0).totalWeight();
        int w2 = algo.computeMST(sampleGraph(), 2).totalWeight();
        int w4 = algo.computeMST(sampleGraph(), 4).totalWeight();
        assertEqual(w0, w2, "MST weight from vertex 0 vs 2");
        assertEqual(w0, w4, "MST weight from vertex 0 vs 4");
    }

    private static void testSingleVertex() {
        MstResult result = new PrimsMST().computeMST(Graph.fromMatrix(new int[][]{ { 0 } }));
        assertEqual(0, result.edges().size(), "single-vertex MST has no edges");
        assertEqual(0, result.totalWeight(), "single-vertex MST has zero weight");
    }

    private static void testTwoVertexGraph() {
        Graph graph = Graph.fromMatrix(new int[][]{ { 0, 4 }, { 4, 0 } });
        MstResult result = new PrimsMST().computeMST(graph);
        assertEqual(1, result.edges().size(), "two-vertex MST has one edge");
        assertEqual(4, result.totalWeight(), "two-vertex MST weight");
    }

    private static void testTriangleGraph() {
        // 0-1 (weight 1), 1-2 (weight 2), 0-2 (weight 3) — MST picks 0-1 and 1-2
        Graph graph = Graph.fromMatrix(new int[][]{
            { 0, 1, 3 },
            { 1, 0, 2 },
            { 3, 2, 0 }
        });
        MstResult result = new PrimsMST().computeMST(graph);
        assertEqual(2, result.edges().size(), "triangle MST edge count");
        assertEqual(3, result.totalWeight(), "triangle MST picks two cheapest edges");
    }

    private static void testDisconnectedGraph() {
        int[][] matrix = {
            { 0, 1, 0 },
            { 1, 0, 0 },
            { 0, 0, 0 }
        };
        assertThrows(() -> new PrimsMST().computeMST(Graph.fromMatrix(matrix)),
            "disconnected graph starting from reachable component");
    }

    private static void testDisconnectedFromIsolatedVertex() {
        int[][] matrix = {
            { 0, 1, 0 },
            { 1, 0, 0 },
            { 0, 0, 0 }
        };
        assertThrows(() -> new PrimsMST().computeMST(Graph.fromMatrix(matrix), 2),
            "disconnected graph starting from isolated vertex");
    }

    private static void testNullGraph() {
        assertThrows(() -> new PrimsMST().computeMST(null), "null graph");
    }

    private static void testInvalidStartVertex() {
        int[][] matrix = { { 0, 1 }, { 1, 0 } };
        assertThrows(() -> new PrimsMST().computeMST(Graph.fromMatrix(matrix), 5),
            "out-of-range start vertex");
        assertThrows(() -> new PrimsMST().computeMST(Graph.fromMatrix(matrix), -1),
            "negative start vertex");
    }

    private static void testGraphValidationNonSquare() {
        assertThrows(() -> Graph.fromMatrix(new int[][]{ { 0, 1 }, { 1, 0, 0 } }),
            "non-square matrix");
    }

    private static void testGraphValidationSelfLoop() {
        assertThrows(() -> Graph.fromMatrix(new int[][]{ { 1, 0 }, { 0, 0 } }),
            "self-loop on diagonal");
    }

    private static void testGraphValidationNonSymmetric() {
        assertThrows(() -> Graph.fromMatrix(new int[][]{ { 0, 1 }, { 2, 0 } }),
            "non-symmetric matrix");
    }

    private static void testMstResultEquality() {
        Graph graph = Graph.fromMatrix(new int[][]{ { 0, 5 }, { 5, 0 } });
        MstResult r1 = new PrimsMST().computeMST(graph);
        MstResult r2 = new PrimsMST().computeMST(graph);
        if (!r1.equals(r2)) {
            throw new AssertionError("identical MST computations should be equal");
        }
        if (r1.hashCode() != r2.hashCode()) {
            throw new AssertionError("equal MstResults must have matching hashCode");
        }
        if (r1.equals(new PrimsMST().computeMST(sampleGraph()))) {
            throw new AssertionError("MSTs of different graphs should not be equal");
        }
    }

    private static void testEdgesFromOutOfBounds() {
        Graph graph = Graph.fromMatrix(new int[][]{ { 0, 1 }, { 1, 0 } });
        assertThrows(() -> graph.edgesFrom(-1), "edgesFrom with negative vertex");
        assertThrows(() -> graph.edgesFrom(2), "edgesFrom with vertex beyond range");
    }

    // --- helpers ---

    private static Graph sampleGraph() {
        return Graph.fromMatrix(new int[][]{
            { 0, 2, 0, 6, 0 },
            { 2, 0, 3, 8, 5 },
            { 0, 3, 0, 0, 7 },
            { 6, 8, 0, 0, 9 },
            { 0, 5, 7, 9, 0 }
        });
    }

    private static void assertEqual(int expected, int actual, String context) {
        if (expected != actual) {
            throw new AssertionError(
                context + ": expected " + expected + " but was " + actual);
        }
    }

    private static void assertContainsEdge(List<Edge> edges, int from, int to, int weight) {
        for (Edge e : edges) {
            if (e.from() == from && e.to() == to && e.weight() == weight) {
                return;
            }
        }
        throw new AssertionError(
            "Expected edge " + from + "->" + to + " (weight " + weight + ") not found in " + edges);
    }

    private static void assertThrows(Runnable action, String context) {
        try {
            action.run();
            throw new AssertionError(
                context + ": expected IllegalArgumentException but none was thrown");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }
}
