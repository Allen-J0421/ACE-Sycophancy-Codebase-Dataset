class PrimsMSTTest {

    public static void main(String[] args) {
        testBasicMST();
        testDifferentStartVertex();
        testSingleVertex();
        testDisconnectedGraph();
        testNullGraph();
        testInvalidStartVertex();
        testGraphValidationNonSquare();
        testGraphValidationSelfLoop();
        testGraphValidationNonSymmetric();
        System.out.println("All tests passed.");
    }

    private static void testBasicMST() {
        int[][] matrix = {
            { 0, 2, 0, 6, 0 },
            { 2, 0, 3, 8, 5 },
            { 0, 3, 0, 0, 7 },
            { 6, 8, 0, 0, 9 },
            { 0, 5, 7, 9, 0 }
        };
        MstResult result = new PrimsMST().computeMST(Graph.fromMatrix(matrix));
        assertEqual(4, result.edges().size(), "edge count");
        assertEqual(16, result.totalWeight(), "total weight");
    }

    private static void testDifferentStartVertex() {
        int[][] matrix = {
            { 0, 2, 0, 6, 0 },
            { 2, 0, 3, 8, 5 },
            { 0, 3, 0, 0, 7 },
            { 6, 8, 0, 0, 9 },
            { 0, 5, 7, 9, 0 }
        };
        MstAlgorithm algo = new PrimsMST();
        int weightFrom0 = algo.computeMST(Graph.fromMatrix(matrix), 0).totalWeight();
        int weightFrom2 = algo.computeMST(Graph.fromMatrix(matrix), 2).totalWeight();
        int weightFrom4 = algo.computeMST(Graph.fromMatrix(matrix), 4).totalWeight();
        assertEqual(weightFrom0, weightFrom2, "MST weight is start-vertex-independent (0 vs 2)");
        assertEqual(weightFrom0, weightFrom4, "MST weight is start-vertex-independent (0 vs 4)");
    }

    private static void testSingleVertex() {
        MstResult result = new PrimsMST().computeMST(Graph.fromMatrix(new int[][]{ { 0 } }));
        assertEqual(0, result.edges().size(), "single-vertex MST has no edges");
        assertEqual(0, result.totalWeight(), "single-vertex MST has zero weight");
    }

    private static void testDisconnectedGraph() {
        int[][] matrix = {
            { 0, 1, 0 },
            { 1, 0, 0 },
            { 0, 0, 0 }
        };
        assertThrows(() -> new PrimsMST().computeMST(Graph.fromMatrix(matrix)),
            "disconnected graph");
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

    private static void assertEqual(int expected, int actual, String context) {
        if (expected != actual) {
            throw new AssertionError(
                context + ": expected " + expected + " but was " + actual);
        }
    }

    private static void assertThrows(Runnable action, String context) {
        try {
            action.run();
            throw new AssertionError(
                context + ": expected IllegalArgumentException but no exception was thrown");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }
}
