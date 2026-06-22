import java.util.Arrays;
import java.util.List;

/**
 * Dependency-free test runner for {@link FloydWarshall} and {@link Graph}.
 *
 * <p>Compile and run alongside the rest of the sources:
 * <pre>{@code  javac *.java && java FloydWarshallTest }</pre>
 * Exits with status 0 if every test passes, 1 otherwise.
 */
public final class FloydWarshallTest {

    private static int failures = 0;
    private static final int INF = Graph.INF;

    public static void main(String[] args) {
        knownExampleMatchesExpected();
        unreachableVerticesStayInfinite();
        negativeEdgesWithoutCycleAreHandled();
        negativeCycleIsDetected();
        nonSquareMatrixIsRejected();
        emptyMatrixIsRejected();
        inputGraphIsNotMutated();
        singleVertexGraphIsTrivial();
        pathReconstructionFollowsShortestRoute();
        pathToSelfIsSingleVertex();
        unreachablePathIsEmpty();
        directEdgePathIsTwoVertices();

        if (failures == 0) {
            System.out.println("All tests passed.");
        } else {
            System.out.println(failures + " test(s) failed.");
            System.exit(1);
        }
    }

    private static void knownExampleMatchesExpected() {
        Graph g = Graph.of(new int[][] {
                {0,   4,   INF, 5,   INF},
                {INF, 0,   1,   INF, 6},
                {2,   INF, 0,   3,   INF},
                {INF, INF, 1,   0,   2},
                {1,   INF, INF, 4,   0}
        });
        int[][] expected = {
                {0, 4, 5, 5, 7},
                {3, 0, 1, 4, 6},
                {2, 6, 0, 3, 5},
                {3, 7, 1, 0, 2},
                {1, 5, 5, 4, 0}
        };
        check("known example", expected, FloydWarshall.shortestPaths(g).distances().toMatrix());
    }

    private static void unreachableVerticesStayInfinite() {
        // Vertex 1 has no outgoing edges and cannot reach vertex 0.
        Graph g = Graph.of(new int[][] {
                {0,   7},
                {INF, 0}
        });
        int[][] expected = {
                {0,   7},
                {INF, 0}
        };
        check("unreachable stays INF", expected, FloydWarshall.shortestPaths(g).distances().toMatrix());
    }

    private static void negativeEdgesWithoutCycleAreHandled() {
        Graph g = Graph.of(new int[][] {
                {0,   -2,  INF},
                {INF, 0,   3},
                {INF, INF, 0}
        });
        int[][] expected = {
                {0,   -2,  1},
                {INF, 0,   3},
                {INF, INF, 0}
        };
        check("negative edges, no cycle", expected, FloydWarshall.shortestPaths(g).distances().toMatrix());
    }

    private static void negativeCycleIsDetected() {
        Graph g = Graph.of(new int[][] {
                {0,  1},
                {-3, 0}   // 0->1->0 totals -2: a negative cycle
        });
        checkThrows("negative cycle detection", () -> FloydWarshall.shortestPaths(g));
    }

    private static void nonSquareMatrixIsRejected() {
        checkThrows("non-square rejected", () -> Graph.of(new int[][] {
                {0, 1, 2},
                {3, 4}
        }));
    }

    private static void emptyMatrixIsRejected() {
        checkThrows("empty rejected", () -> Graph.of(new int[][] {}));
    }

    private static void inputGraphIsNotMutated() {
        Graph g = Graph.of(new int[][] {
                {0,   4},
                {INF, 0}
        });
        int[][] before = g.toMatrix();
        FloydWarshall.shortestPaths(g);
        check("input not mutated", before, g.toMatrix());
    }

    private static void singleVertexGraphIsTrivial() {
        Graph g = Graph.of(new int[][] {{0}});
        check("single vertex", new int[][] {{0}}, FloydWarshall.shortestPaths(g).distances().toMatrix());
    }

    private static ShortestPaths knownExample() {
        return FloydWarshall.shortestPaths(Graph.of(new int[][] {
                {0,   4,   INF, 5,   INF},
                {INF, 0,   1,   INF, 6},
                {2,   INF, 0,   3,   INF},
                {INF, INF, 1,   0,   2},
                {1,   INF, INF, 4,   0}
        }));
    }

    private static void pathReconstructionFollowsShortestRoute() {
        // 1 -> 0 has no direct edge; the shortest route is 1 ->2 ->0 (1 + 2 = 3).
        check("path follows shortest route",
                List.of(1, 2, 0), knownExample().path(1, 0));
    }

    private static void pathToSelfIsSingleVertex() {
        check("path to self", List.of(2), knownExample().path(2, 2));
    }

    private static void unreachablePathIsEmpty() {
        Graph g = Graph.of(new int[][] {
                {0,   7},
                {INF, 0}
        });
        ShortestPaths sp = FloydWarshall.shortestPaths(g);
        check("unreachable path is empty", List.of(), sp.path(1, 0));
        if (sp.hasPath(1, 0)) {
            failures++;
            System.out.println("FAIL: hasPath false for unreachable");
        } else {
            System.out.println("PASS: hasPath false for unreachable");
        }
    }

    private static void directEdgePathIsTwoVertices() {
        check("direct edge path", List.of(0, 1), knownExample().path(0, 1));
    }

    // --- tiny assertion helpers ---

    private static void check(String name, int[][] expected, int[][] actual) {
        if (Arrays.deepEquals(expected, actual)) {
            System.out.println("PASS: " + name);
        } else {
            failures++;
            System.out.println("FAIL: " + name);
            System.out.println("  expected: " + Arrays.deepToString(expected));
            System.out.println("  actual:   " + Arrays.deepToString(actual));
        }
    }

    private static void check(String name, List<Integer> expected, List<Integer> actual) {
        if (expected.equals(actual)) {
            System.out.println("PASS: " + name);
        } else {
            failures++;
            System.out.println("FAIL: " + name);
            System.out.println("  expected: " + expected);
            System.out.println("  actual:   " + actual);
        }
    }

    private static void checkThrows(String name, Runnable action) {
        try {
            action.run();
            failures++;
            System.out.println("FAIL: " + name + " (expected IllegalArgumentException)");
        } catch (IllegalArgumentException expected) {
            System.out.println("PASS: " + name);
        }
    }
}
