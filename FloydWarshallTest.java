import java.util.List;

/**
 * Dependency-free test runner for {@link FloydWarshall} and {@link Graph}.
 * Assertion plumbing lives in {@link TestAssertions}.
 *
 * <p>Compile and run alongside the rest of the sources:
 * <pre>{@code  javac *.java && java FloydWarshallTest }</pre>
 * Exits with status 0 if every test passes, 1 otherwise.
 */
public final class FloydWarshallTest {

    private static final int INF = Graph.INF;
    private static final TestAssertions t = new TestAssertions();

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
        graphRejectsOutOfRangeVertex();
        shortestPathsRejectOutOfRangeVertex();
        neighborsListDirectEdges();
        vertexWithNoOutgoingEdgesHasNoNeighbors();
        neighborsRejectOutOfRangeVertex();
        builderProducesEquivalentGraph();
        builderRejectsInvalidEdges();
        builderRejectsNonPositiveVertexCount();

        t.report();
        if (!t.allPassed()) {
            System.exit(1);
        }
    }

    private static void knownExampleMatchesExpected() {
        int[][] expected = {
                {0, 4, 5, 5, 7},
                {3, 0, 1, 4, 6},
                {2, 6, 0, 3, 5},
                {3, 7, 1, 0, 2},
                {1, 5, 5, 4, 0}
        };
        t.equal("known example", expected, knownExample().distances().toMatrix());
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
        t.equal("unreachable stays INF", expected,
                FloydWarshall.shortestPaths(g).distances().toMatrix());
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
        t.equal("negative edges, no cycle", expected,
                FloydWarshall.shortestPaths(g).distances().toMatrix());
    }

    private static void negativeCycleIsDetected() {
        Graph g = Graph.of(new int[][] {
                {0,  1},
                {-3, 0}   // 0->1->0 totals -2: a negative cycle
        });
        t.throwsIllegalArgument("negative cycle detection", () -> FloydWarshall.shortestPaths(g));
    }

    private static void nonSquareMatrixIsRejected() {
        t.throwsIllegalArgument("non-square rejected", () -> Graph.of(new int[][] {
                {0, 1, 2},
                {3, 4}
        }));
    }

    private static void emptyMatrixIsRejected() {
        t.throwsIllegalArgument("empty rejected", () -> Graph.of(new int[][] {}));
    }

    private static void inputGraphIsNotMutated() {
        Graph g = Graph.of(new int[][] {
                {0,   4},
                {INF, 0}
        });
        int[][] before = g.toMatrix();
        FloydWarshall.shortestPaths(g);
        t.equal("input not mutated", before, g.toMatrix());
    }

    private static void singleVertexGraphIsTrivial() {
        Graph g = Graph.of(new int[][] {{0}});
        t.equal("single vertex", new int[][] {{0}},
                FloydWarshall.shortestPaths(g).distances().toMatrix());
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
        t.equal("path follows shortest route", List.of(1, 2, 0), knownExample().path(1, 0));
    }

    private static void pathToSelfIsSingleVertex() {
        t.equal("path to self", List.of(2), knownExample().path(2, 2));
    }

    private static void unreachablePathIsEmpty() {
        Graph g = Graph.of(new int[][] {
                {0,   7},
                {INF, 0}
        });
        ShortestPaths sp = FloydWarshall.shortestPaths(g);
        t.equal("unreachable path is empty", List.of(), sp.path(1, 0));
        t.isTrue("hasPath false for unreachable", !sp.hasPath(1, 0));
    }

    private static void directEdgePathIsTwoVertices() {
        t.equal("direct edge path", List.of(0, 1), knownExample().path(0, 1));
    }

    private static void neighborsListDirectEdges() {
        Graph g = Graph.of(new int[][] {
                {0,   4,   INF, 5,   INF},
                {INF, 0,   1,   INF, 6},
                {2,   INF, 0,   3,   INF},
                {INF, INF, 1,   0,   2},
                {1,   INF, INF, 4,   0}
        });
        t.equal("neighbors of vertex 0", List.of(1, 3), g.neighbors(0));
        t.equal("neighbors of vertex 1", List.of(2, 4), g.neighbors(1));
        // Self is never a neighbor even though the diagonal weight is 0.
        t.isTrue("vertex is not its own neighbor", !g.neighbors(2).contains(2));
    }

    private static void vertexWithNoOutgoingEdgesHasNoNeighbors() {
        Graph g = Graph.of(new int[][] {
                {0,   7},
                {INF, 0}
        });
        t.equal("no outgoing edges", List.of(), g.neighbors(1));
    }

    private static void neighborsRejectOutOfRangeVertex() {
        Graph g = Graph.of(new int[][] {{0}});
        t.throwsIndexOutOfBounds("neighbors rejects out-of-range vertex", () -> g.neighbors(1));
    }

    private static void builderProducesEquivalentGraph() {
        Graph viaMatrix = Graph.of(new int[][] {
                {0,   4,   INF, 5,   INF},
                {INF, 0,   1,   INF, 6},
                {2,   INF, 0,   3,   INF},
                {INF, INF, 1,   0,   2},
                {1,   INF, INF, 4,   0}
        });
        Graph viaBuilder = Graph.builder(5)
                .addEdge(0, 1, 4)
                .addEdge(0, 3, 5)
                .addEdge(1, 2, 1)
                .addEdge(1, 4, 6)
                .addEdge(2, 0, 2)
                .addEdge(2, 3, 3)
                .addEdge(3, 2, 1)
                .addEdge(3, 4, 2)
                .addEdge(4, 0, 1)
                .addEdge(4, 3, 4)
                .build();
        t.isTrue("builder equals matrix construction", viaMatrix.equals(viaBuilder));
    }

    private static void builderRejectsInvalidEdges() {
        t.throwsIndexOutOfBounds("builder rejects out-of-range vertex",
                () -> Graph.builder(2).addEdge(0, 2, 1));
        t.throwsIllegalArgument("builder rejects self-loop",
                () -> Graph.builder(2).addEdge(1, 1, 1));
        t.throwsIllegalArgument("builder rejects INF weight",
                () -> Graph.builder(2).addEdge(0, 1, Graph.INF));
    }

    private static void builderRejectsNonPositiveVertexCount() {
        t.throwsIllegalArgument("builder rejects zero vertices", () -> Graph.builder(0));
    }

    private static void graphRejectsOutOfRangeVertex() {
        Graph g = Graph.of(new int[][] {
                {0,   4},
                {INF, 0}
        });
        t.throwsIndexOutOfBounds("graph weight rejects negative vertex", () -> g.weight(-1, 0));
        t.throwsIndexOutOfBounds("graph weight rejects too-large vertex", () -> g.weight(0, 2));
    }

    private static void shortestPathsRejectOutOfRangeVertex() {
        ShortestPaths sp = knownExample();
        t.throwsIndexOutOfBounds("distance rejects out-of-range vertex", () -> sp.distance(0, 5));
        t.throwsIndexOutOfBounds("hasPath rejects out-of-range vertex", () -> sp.hasPath(-1, 0));
        t.throwsIndexOutOfBounds("path rejects out-of-range vertex", () -> sp.path(0, 99));
    }
}
