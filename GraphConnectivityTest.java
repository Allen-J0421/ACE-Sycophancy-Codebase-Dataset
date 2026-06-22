import java.util.ArrayList;
import java.util.List;

/**
 * A small, dependency-free test runner for {@link GraphConnectivity} and
 * {@link Graph}. Run with {@code java GraphConnectivityTest}; it prints a summary
 * and exits with a non-zero status if any test fails.
 */
public final class GraphConnectivityTest {

    private int passed;
    private int failed;

    public static void main(String[] args) {
        GraphConnectivityTest tests = new GraphConnectivityTest();
        tests.run();
        System.out.printf("%n%d passed, %d failed%n", tests.passed, tests.failed);
        if (tests.failed > 0) {
            System.exit(1);
        }
    }

    private void run() {
        // Articulation points.
        emptyGraphHasNoArticulationPoints();
        singleVertexHasNoArticulationPoints();
        triangleHasNoArticulationPoints();
        pathExposesInteriorVertices();
        starCenterIsArticulationPoint();
        disconnectedComponentsHandledIndependently();
        rootIsArticulationPointOnlyWithMultipleChildren();
        exampleFromMainMatchesExpectedOutput();

        // Bridges.
        pathEdgesAreAllBridges();
        triangleHasNoBridges();
        starEdgesAreAllBridges();
        exampleFromMainBridges();
        disconnectedComponentBridges();

        // Multigraph cases: parallel edges and self-loops. Parallel edges leave the
        // articulation-point verdict unchanged but DO disqualify bridges (whose test
        // is strict), which is the whole reason edges carry identity.
        parallelEdgesBetweenTwoVerticesHaveNoArticulationPoint();
        parallelEdgesBetweenTwoVerticesHaveNoBridge();
        parallelEdgesAlongPathPreserveInteriorCutVertices();
        parallelEdgeIsNotABridgeButFlankingEdgesAre();
        parallelEdgeAcrossTriangleHasNoArticulationPoint();
        parallelEdgeAcrossTriangleHasNoBridges();
        selfLoopIsIgnored();

        // Robustness.
        negativeVertexCountIsRejected();
        outOfRangeEdgeIsRejected();
        malformedEdgeIsRejected();
        analyzerIsReusableAcrossGraphs();
        deepGraphDoesNotOverflowTheStack();
    }

    // --- Articulation points -------------------------------------------------

    private void emptyGraphHasNoArticulationPoints() {
        check("empty graph", articulationPoints(new Graph(0)), List.of());
    }

    private void singleVertexHasNoArticulationPoints() {
        check("single vertex", articulationPoints(new Graph(1)), List.of());
    }

    private void triangleHasNoArticulationPoints() {
        Graph triangle = Graph.fromEdges(3, new int[][] {{0, 1}, {1, 2}, {2, 0}});
        check("triangle (AP)", articulationPoints(triangle), List.of());
    }

    private void pathExposesInteriorVertices() {
        Graph path = Graph.fromEdges(4, new int[][] {{0, 1}, {1, 2}, {2, 3}});
        check("path 0-1-2-3 (AP)", articulationPoints(path), List.of(1, 2));
    }

    private void starCenterIsArticulationPoint() {
        Graph star = Graph.fromEdges(4, new int[][] {{0, 1}, {0, 2}, {0, 3}});
        check("star centered at 0 (AP)", articulationPoints(star), List.of(0));
    }

    private void disconnectedComponentsHandledIndependently() {
        Graph forest = Graph.fromEdges(6, new int[][] {{0, 1}, {1, 2}, {3, 4}, {4, 5}});
        check("disconnected paths (AP)", articulationPoints(forest), List.of(1, 4));
    }

    private void rootIsArticulationPointOnlyWithMultipleChildren() {
        Graph graph = Graph.fromEdges(5, new int[][] {{0, 1}, {1, 2}, {0, 3}, {3, 4}});
        check("root with two children (AP)", articulationPoints(graph), List.of(0, 1, 3));
    }

    private void exampleFromMainMatchesExpectedOutput() {
        Graph graph = Graph.fromEdges(5, new int[][] {{0, 1}, {1, 4}, {2, 3}, {2, 4}, {3, 4}});
        check("Main example (AP)", articulationPoints(graph), List.of(1, 4));
    }

    // --- Bridges -------------------------------------------------------------

    private void pathEdgesAreAllBridges() {
        Graph path = Graph.fromEdges(4, new int[][] {{0, 1}, {1, 2}, {2, 3}});
        check("path 0-1-2-3 (bridges)", bridges(path), List.of("0-1", "1-2", "2-3"));
    }

    private void triangleHasNoBridges() {
        Graph triangle = Graph.fromEdges(3, new int[][] {{0, 1}, {1, 2}, {2, 0}});
        check("triangle (bridges)", bridges(triangle), List.of());
    }

    private void starEdgesAreAllBridges() {
        Graph star = Graph.fromEdges(4, new int[][] {{0, 1}, {0, 2}, {0, 3}});
        check("star centered at 0 (bridges)", bridges(star), List.of("0-1", "0-2", "0-3"));
    }

    private void exampleFromMainBridges() {
        Graph graph = Graph.fromEdges(5, new int[][] {{0, 1}, {1, 4}, {2, 3}, {2, 4}, {3, 4}});
        check("Main example (bridges)", bridges(graph), List.of("0-1", "1-4"));
    }

    private void disconnectedComponentBridges() {
        Graph forest = Graph.fromEdges(6, new int[][] {{0, 1}, {1, 2}, {3, 4}, {4, 5}});
        check("disconnected paths (bridges)", bridges(forest),
                List.of("0-1", "1-2", "3-4", "4-5"));
    }

    // --- Multigraphs ---------------------------------------------------------

    private void parallelEdgesBetweenTwoVerticesHaveNoArticulationPoint() {
        Graph graph = Graph.fromEdges(2, new int[][] {{0, 1}, {0, 1}});
        check("parallel edges, two vertices (AP)", articulationPoints(graph), List.of());
    }

    private void parallelEdgesBetweenTwoVerticesHaveNoBridge() {
        // A single 0-1 edge is a bridge; doubling it removes that.
        Graph graph = Graph.fromEdges(2, new int[][] {{0, 1}, {0, 1}});
        check("parallel edges, two vertices (bridges)", bridges(graph), List.of());
    }

    private void parallelEdgesAlongPathPreserveInteriorCutVertices() {
        Graph graph = Graph.fromEdges(4, new int[][] {{0, 1}, {1, 2}, {1, 2}, {2, 3}});
        check("parallel edge on a path (AP)", articulationPoints(graph), List.of(1, 2));
    }

    private void parallelEdgeIsNotABridgeButFlankingEdgesAre() {
        // The doubled 1-2 edge is NOT a bridge (its twin keeps 1 and 2 connected),
        // while the single 0-1 and 2-3 edges still are. This is the case that fails
        // without per-edge identity.
        Graph graph = Graph.fromEdges(4, new int[][] {{0, 1}, {1, 2}, {1, 2}, {2, 3}});
        check("parallel edge on a path (bridges)", bridges(graph), List.of("0-1", "2-3"));
    }

    private void parallelEdgeAcrossTriangleHasNoArticulationPoint() {
        Graph graph = Graph.fromEdges(3, new int[][] {{0, 1}, {1, 2}, {2, 0}, {2, 0}});
        check("parallel edge across a triangle (AP)", articulationPoints(graph), List.of());
    }

    private void parallelEdgeAcrossTriangleHasNoBridges() {
        Graph graph = Graph.fromEdges(3, new int[][] {{0, 1}, {1, 2}, {2, 0}, {2, 0}});
        check("parallel edge across a triangle (bridges)", bridges(graph), List.of());
    }

    private void selfLoopIsIgnored() {
        Graph graph = Graph.fromEdges(3, new int[][] {{0, 1}, {1, 2}, {1, 1}});
        ConnectivityResult result = new GraphConnectivity().analyze(graph);
        check("self-loop ignored (AP)", result.articulationPoints(), List.of(1));
        check("self-loop ignored (bridges)", bridgeKeys(result), List.of("0-1", "1-2"));
    }

    // --- Robustness ----------------------------------------------------------

    private void negativeVertexCountIsRejected() {
        checkThrows("negative vertex count", IllegalArgumentException.class, () -> new Graph(-1));
    }

    private void outOfRangeEdgeIsRejected() {
        checkThrows("out-of-range edge", IndexOutOfBoundsException.class,
                () -> Graph.fromEdges(2, new int[][] {{0, 5}}));
    }

    private void malformedEdgeIsRejected() {
        checkThrows("malformed edge", IllegalArgumentException.class,
                () -> Graph.fromEdges(2, new int[][] {{0}}));
    }

    private void analyzerIsReusableAcrossGraphs() {
        GraphConnectivity analyzer = new GraphConnectivity();
        Graph path = Graph.fromEdges(3, new int[][] {{0, 1}, {1, 2}});
        Graph triangle = Graph.fromEdges(3, new int[][] {{0, 1}, {1, 2}, {2, 0}});
        check("reuse: path then triangle (1)", analyzer.analyze(path).articulationPoints(),
                List.of(1));
        check("reuse: path then triangle (2)", analyzer.analyze(triangle).articulationPoints(),
                List.of());
    }

    private void deepGraphDoesNotOverflowTheStack() {
        // A long path is the worst case for recursion depth; the iterative
        // traversal must handle it without a StackOverflowError. Every interior
        // vertex of a path is a cut vertex and every edge is a bridge.
        int vertexCount = 200_000;
        int[][] edges = new int[vertexCount - 1][2];
        for (int i = 0; i < vertexCount - 1; i++) {
            edges[i] = new int[] {i, i + 1};
        }
        ConnectivityResult result = new GraphConnectivity().analyze(Graph.fromEdges(vertexCount, edges));
        List<Integer> points = result.articulationPoints();
        boolean correct = points.size() == vertexCount - 2
                && points.get(0) == 1
                && points.get(points.size() - 1) == vertexCount - 2
                && result.bridges().size() == vertexCount - 1;
        check("deep path of " + vertexCount + " vertices", correct, true);
    }

    // --- Helpers -------------------------------------------------------------

    private static List<Integer> articulationPoints(Graph graph) {
        return new GraphConnectivity().analyze(graph).articulationPoints();
    }

    /** Returns the bridges as sorted {@code "min-max"} endpoint keys. */
    private static List<String> bridges(Graph graph) {
        return bridgeKeys(new GraphConnectivity().analyze(graph));
    }

    private static List<String> bridgeKeys(ConnectivityResult result) {
        List<String> keys = new ArrayList<>();
        for (Graph.Edge edge : result.bridges()) {
            keys.add(Math.min(edge.u(), edge.v()) + "-" + Math.max(edge.u(), edge.v()));
        }
        return keys;
    }

    private void check(String name, Object actual, Object expected) {
        if (expected.equals(actual)) {
            passed++;
            System.out.println("PASS " + name);
        } else {
            failed++;
            System.out.println("FAIL " + name + ": expected " + expected + " but was " + actual);
        }
    }

    private void checkThrows(String name, Class<? extends Throwable> expected, Runnable action) {
        try {
            action.run();
            failed++;
            System.out.println("FAIL " + name + ": expected " + expected.getSimpleName()
                    + " but nothing was thrown");
        } catch (Throwable thrown) {
            if (expected.isInstance(thrown)) {
                passed++;
                System.out.println("PASS " + name);
            } else {
                failed++;
                System.out.println("FAIL " + name + ": expected " + expected.getSimpleName()
                        + " but threw " + thrown.getClass().getSimpleName());
            }
        }
    }
}
