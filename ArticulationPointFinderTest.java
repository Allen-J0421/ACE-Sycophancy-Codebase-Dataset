import java.util.List;

/**
 * A small, dependency-free test runner for {@link ArticulationPointFinder} and
 * {@link Graph}. Run with {@code java ArticulationPointFinderTest}; it prints a
 * summary and exits with a non-zero status if any test fails.
 */
public final class ArticulationPointFinderTest {

    private int passed;
    private int failed;

    public static void main(String[] args) {
        ArticulationPointFinderTest tests = new ArticulationPointFinderTest();
        tests.run();
        System.out.printf("%n%d passed, %d failed%n", tests.passed, tests.failed);
        if (tests.failed > 0) {
            System.exit(1);
        }
    }

    private void run() {
        emptyGraphHasNoArticulationPoints();
        singleVertexHasNoArticulationPoints();
        triangleHasNoArticulationPoints();
        pathExposesInteriorVertices();
        starCenterIsArticulationPoint();
        disconnectedComponentsHandledIndependently();
        rootIsArticulationPointOnlyWithMultipleChildren();
        exampleFromMainMatchesExpectedOutput();
        negativeVertexCountIsRejected();
        outOfRangeEdgeIsRejected();
        malformedEdgeIsRejected();
        finderIsReusableAcrossGraphs();
    }

    private void emptyGraphHasNoArticulationPoints() {
        check("empty graph", find(new Graph(0)), List.of());
    }

    private void singleVertexHasNoArticulationPoints() {
        check("single vertex", find(new Graph(1)), List.of());
    }

    private void triangleHasNoArticulationPoints() {
        Graph triangle = Graph.fromEdges(3, new int[][] {{0, 1}, {1, 2}, {2, 0}});
        check("triangle", find(triangle), List.of());
    }

    private void pathExposesInteriorVertices() {
        Graph path = Graph.fromEdges(4, new int[][] {{0, 1}, {1, 2}, {2, 3}});
        check("path 0-1-2-3", find(path), List.of(1, 2));
    }

    private void starCenterIsArticulationPoint() {
        Graph star = Graph.fromEdges(4, new int[][] {{0, 1}, {0, 2}, {0, 3}});
        check("star centered at 0", find(star), List.of(0));
    }

    private void disconnectedComponentsHandledIndependently() {
        // Two separate paths: 0-1-2 and 3-4-5; the middle of each is a cut vertex.
        Graph forest = Graph.fromEdges(6, new int[][] {{0, 1}, {1, 2}, {3, 4}, {4, 5}});
        check("disconnected paths", find(forest), List.of(1, 4));
    }

    private void rootIsArticulationPointOnlyWithMultipleChildren() {
        // Root 0 bridges two otherwise-separate subtrees, so it is a cut vertex.
        Graph graph = Graph.fromEdges(5, new int[][] {{0, 1}, {1, 2}, {0, 3}, {3, 4}});
        check("root with two children", find(graph), List.of(0, 1, 3));
    }

    private void exampleFromMainMatchesExpectedOutput() {
        Graph graph = Graph.fromEdges(5, new int[][] {{0, 1}, {1, 4}, {2, 3}, {2, 4}, {3, 4}});
        check("Main example", find(graph), List.of(1, 4));
    }

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

    private void finderIsReusableAcrossGraphs() {
        ArticulationPointFinder finder = new ArticulationPointFinder();
        Graph path = Graph.fromEdges(3, new int[][] {{0, 1}, {1, 2}});
        Graph triangle = Graph.fromEdges(3, new int[][] {{0, 1}, {1, 2}, {2, 0}});
        check("reuse: path then triangle (1)", finder.find(path), List.of(1));
        check("reuse: path then triangle (2)", finder.find(triangle), List.of());
    }

    private static List<Integer> find(Graph graph) {
        return new ArticulationPointFinder().find(graph);
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
