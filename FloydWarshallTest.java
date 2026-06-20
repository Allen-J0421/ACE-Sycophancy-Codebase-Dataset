import java.util.List;

public class FloydWarshallTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        testBasicDistances();
        testPathReconstruction();
        testUnreachableVertices();
        testSingleVertex();
        testNegativeEdgesWithoutCycle();
        testNegativeCycleDetection();
        testInputValidation();
        testInputNotMutated();

        System.out.printf("Tests: %d passed, %d failed%n", passed, failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void assertThrows(String name, Class<? extends Exception> type, Runnable block) {
        try {
            block.run();
            failed++;
            System.err.printf("FAIL [%s]: expected %s%n", name, type.getSimpleName());
        } catch (Exception e) {
            if (type.isInstance(e)) {
                passed++;
            } else {
                failed++;
                System.err.printf("FAIL [%s]: expected %s but got %s%n",
                    name, type.getSimpleName(), e.getClass().getSimpleName());
            }
        }
    }

    private static void assertDistMatrix(FloydWarshall.Result r, int[][] expected) {
        for (int i = 0; i < expected.length; i++) {
            for (int j = 0; j < expected[i].length; j++) {
                assertEqual("dist[" + i + "][" + j + "]", expected[i][j], r.getDistance(i, j));
            }
        }
    }

    private static void assertEqual(String name, int expected, int actual) {
        if (expected == actual) {
            passed++;
        } else {
            failed++;
            System.err.printf("FAIL [%s]: expected %d, got %d%n", name, expected, actual);
        }
    }

    private static void assertTrue(String name, boolean condition) {
        if (condition) {
            passed++;
        } else {
            failed++;
            System.err.printf("FAIL [%s]: condition was false%n", name);
        }
    }

    private static int[][] sampleGraph() {
        int I = FloydWarshall.INF;
        return new int[][]{
            {0, 4, I, 5, I},
            {I, 0, 1, I, 6},
            {2, I, 0, 3, I},
            {I, I, 1, 0, 2},
            {1, I, I, 4, 0}
        };
    }

    private static void testBasicDistances() {
        FloydWarshall.Result r = FloydWarshall.compute(sampleGraph());
        assertEqual("vertexCount", 5, r.vertexCount());
        assertDistMatrix(r, new int[][]{
            {0, 4, 5, 5, 7},
            {3, 0, 1, 4, 6},
            {2, 6, 0, 3, 5},
            {3, 7, 1, 0, 2},
            {1, 5, 5, 4, 0}
        });
    }

    private static void testPathReconstruction() {
        FloydWarshall.Result r = FloydWarshall.compute(sampleGraph());
        assertTrue("path[0->2]", r.getPath(0, 2).equals(List.of(0, 1, 2)));  // via 1
        assertTrue("path[1->0]", r.getPath(1, 0).equals(List.of(1, 2, 0)));  // via 2
        assertTrue("path[3->3]", r.getPath(3, 3).equals(List.of(3)));        // self
    }

    private static void testUnreachableVertices() {
        int I = FloydWarshall.INF;
        int[][] graph = {
            {0, 1},
            {I, 0}
        };
        FloydWarshall.Result r = FloydWarshall.compute(graph);
        assertTrue("0->1 reachable", r.isReachable(0, 1));
        assertTrue("1->0 unreachable", !r.isReachable(1, 0));
        assertTrue("1->0 empty path", r.getPath(1, 0).isEmpty());
    }

    private static void testSingleVertex() {
        FloydWarshall.Result r = FloydWarshall.compute(new int[][]{{0}});
        assertEqual("single dist[0][0]", 0, r.getDistance(0, 0));
        assertEqual("single vertexCount", 1, r.vertexCount());
        assertTrue("single path [0]", r.getPath(0, 0).equals(List.of(0)));
    }

    private static void testNegativeEdgesWithoutCycle() {
        int I = FloydWarshall.INF;
        // negative edge 0->1 but no cycle — should compute correctly
        int[][] graph = {
            {0, -1, I},
            {I,  0, 2},
            {I,  I, 0}
        };
        FloydWarshall.Result r = FloydWarshall.compute(graph);
        assertEqual("negEdge dist[0][1]", -1, r.getDistance(0, 1));
        assertEqual("negEdge dist[0][2]",  1, r.getDistance(0, 2));  // 0->1->2: -1+2=1
        assertTrue("negEdge path[0->2]", r.getPath(0, 2).equals(List.of(0, 1, 2)));
    }

    private static void testNegativeCycleDetection() {
        int I = FloydWarshall.INF;
        // 0->1 (+1), 1->2 (+1), 2->0 (-3): total cycle weight -1
        int[][] graph = {
            {0, 1, I},
            {I, 0, 1},
            {-3, I, 0}
        };
        assertThrows("negCycle", IllegalArgumentException.class,
            () -> FloydWarshall.compute(graph));
    }

    private static void testInputValidation() {
        assertThrows("nullGraph", IllegalArgumentException.class,
            () -> FloydWarshall.compute(null));
        assertThrows("nullRow", IllegalArgumentException.class,
            () -> FloydWarshall.compute(new int[][]{null, {0, 0}}));
        assertThrows("emptyGraph", IllegalArgumentException.class,
            () -> FloydWarshall.compute(new int[0][]));
        assertThrows("nonSquare", IllegalArgumentException.class,
            () -> FloydWarshall.compute(new int[][]{{0, 1}, {2}}));

        FloydWarshall.Result r = FloydWarshall.compute(new int[][]{{0}});
        assertThrows("vertexTooHigh", IndexOutOfBoundsException.class,
            () -> r.getDistance(0, 5));
        assertThrows("vertexNegative", IndexOutOfBoundsException.class,
            () -> r.getDistance(-1, 0));
        assertThrows("pathOutOfBounds", IndexOutOfBoundsException.class,
            () -> r.getPath(0, 5));
    }

    private static void testInputNotMutated() {
        int I = FloydWarshall.INF;
        int[][] graph = {
            {0, I, 5},
            {I, 0, 1},
            {I, I, 0}
        };
        int[][] snapshot = {{0, I, 5}, {I, 0, 1}, {I, I, 0}};
        FloydWarshall.compute(graph);
        for (int i = 0; i < snapshot.length; i++) {
            for (int j = 0; j < snapshot[i].length; j++) {
                assertEqual("input[" + i + "][" + j + "] unchanged", snapshot[i][j], graph[i][j]);
            }
        }
    }
}
