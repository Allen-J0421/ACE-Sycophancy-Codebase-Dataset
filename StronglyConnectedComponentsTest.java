import java.util.List;

/**
 * A small, dependency-free test runner for {@link StronglyConnectedComponents}
 * and {@link DirectedGraph}. Run with {@code java StronglyConnectedComponentsTest};
 * it prints a summary and exits with a non-zero status if any test fails.
 */
public final class StronglyConnectedComponentsTest {

    private int passed;
    private int failed;

    public static void main(String[] args) {
        StronglyConnectedComponentsTest tests = new StronglyConnectedComponentsTest();
        tests.run();
        System.out.printf("%n%d passed, %d failed%n", tests.passed, tests.failed);
        if (tests.failed > 0) {
            System.exit(1);
        }
    }

    private void run() {
        emptyGraphHasNoComponents();
        singleVertexIsItsOwnComponent();
        selfLoopIsItsOwnComponent();
        simpleCycleIsOneComponent();
        acyclicChainIsAllSingletons();
        cycleWithTailSplitsCorrectly();
        nestedCyclesSplitCorrectly();
        disconnectedGraphHandledIndependently();
        parallelArcsDoNotChangeComponents();
        twoVerticesNeedBothDirectionsToMerge();

        negativeVertexCountIsRejected();
        outOfRangeArcIsRejected();
        malformedArcIsRejected();
        analyzerIsReusableAcrossGraphs();

        deepChainDoesNotOverflowTheStack();
        deepCycleDoesNotOverflowTheStack();
    }

    private void emptyGraphHasNoComponents() {
        check("empty graph", scc(new DirectedGraph(0)), List.of());
    }

    private void singleVertexIsItsOwnComponent() {
        check("single vertex", scc(new DirectedGraph(1)), List.of(List.of(0)));
    }

    private void selfLoopIsItsOwnComponent() {
        DirectedGraph graph = DirectedGraph.fromEdges(1, new int[][] {{0, 0}});
        check("self-loop", scc(graph), List.of(List.of(0)));
    }

    private void simpleCycleIsOneComponent() {
        DirectedGraph graph = DirectedGraph.fromEdges(3, new int[][] {{0, 1}, {1, 2}, {2, 0}});
        check("3-cycle", scc(graph), List.of(List.of(0, 1, 2)));
    }

    private void acyclicChainIsAllSingletons() {
        DirectedGraph graph = DirectedGraph.fromEdges(3, new int[][] {{0, 1}, {1, 2}});
        check("acyclic chain", scc(graph), List.of(List.of(0), List.of(1), List.of(2)));
    }

    private void cycleWithTailSplitsCorrectly() {
        // {0,1,2} form a cycle; 3 and 4 hang off it as a tail.
        DirectedGraph graph =
                DirectedGraph.fromEdges(5, new int[][] {{1, 0}, {0, 2}, {2, 1}, {0, 3}, {3, 4}});
        check("cycle with tail", scc(graph),
                List.of(List.of(0, 1, 2), List.of(3), List.of(4)));
    }

    private void nestedCyclesSplitCorrectly() {
        // {0,1,2} cycle -> {3,4} cycle -> {5} self-loop.
        DirectedGraph graph = DirectedGraph.fromEdges(6, new int[][] {
                {0, 1}, {1, 2}, {2, 0}, {2, 3}, {3, 4}, {4, 3}, {4, 5}, {5, 5}});
        check("nested cycles", scc(graph),
                List.of(List.of(0, 1, 2), List.of(3, 4), List.of(5)));
    }

    private void disconnectedGraphHandledIndependently() {
        // Two independent cycles with no arcs between them.
        DirectedGraph graph = DirectedGraph.fromEdges(4, new int[][] {{0, 1}, {1, 0}, {2, 3}, {3, 2}});
        check("disconnected cycles", scc(graph), List.of(List.of(0, 1), List.of(2, 3)));
    }

    private void parallelArcsDoNotChangeComponents() {
        // Doubling an arc is harmless; the cycle still yields one component.
        DirectedGraph graph =
                DirectedGraph.fromEdges(2, new int[][] {{0, 1}, {0, 1}, {1, 0}});
        check("parallel arcs", scc(graph), List.of(List.of(0, 1)));
    }

    private void twoVerticesNeedBothDirectionsToMerge() {
        // A single arc 0->1 leaves them in separate components.
        DirectedGraph graph = DirectedGraph.fromEdges(2, new int[][] {{0, 1}});
        check("one-way arc", scc(graph), List.of(List.of(0), List.of(1)));
    }

    private void negativeVertexCountIsRejected() {
        checkThrows("negative vertex count", IllegalArgumentException.class,
                () -> new DirectedGraph(-1));
    }

    private void outOfRangeArcIsRejected() {
        checkThrows("out-of-range arc", IndexOutOfBoundsException.class,
                () -> DirectedGraph.fromEdges(2, new int[][] {{0, 5}}));
    }

    private void malformedArcIsRejected() {
        checkThrows("malformed arc", IllegalArgumentException.class,
                () -> DirectedGraph.fromEdges(2, new int[][] {{0}}));
    }

    private void analyzerIsReusableAcrossGraphs() {
        StronglyConnectedComponents analyzer = new StronglyConnectedComponents();
        DirectedGraph cycle = DirectedGraph.fromEdges(2, new int[][] {{0, 1}, {1, 0}});
        DirectedGraph chain = DirectedGraph.fromEdges(2, new int[][] {{0, 1}});
        check("reuse: cycle then chain (1)", analyzer.find(cycle), List.of(List.of(0, 1)));
        check("reuse: cycle then chain (2)", analyzer.find(chain),
                List.of(List.of(0), List.of(1)));
    }

    private void deepChainDoesNotOverflowTheStack() {
        int vertexCount = 200_000;
        int[][] arcs = new int[vertexCount - 1][2];
        for (int i = 0; i < vertexCount - 1; i++) {
            arcs[i] = new int[] {i, i + 1};
        }
        List<List<Integer>> components = scc(DirectedGraph.fromEdges(vertexCount, arcs));
        boolean correct = components.size() == vertexCount
                && components.get(0).equals(List.of(0))
                && components.get(components.size() - 1).equals(List.of(vertexCount - 1));
        check("deep acyclic chain of " + vertexCount, correct, true);
    }

    private void deepCycleDoesNotOverflowTheStack() {
        int vertexCount = 200_000;
        int[][] arcs = new int[vertexCount][2];
        for (int i = 0; i < vertexCount; i++) {
            arcs[i] = new int[] {i, (i + 1) % vertexCount};
        }
        List<List<Integer>> components = scc(DirectedGraph.fromEdges(vertexCount, arcs));
        boolean correct = components.size() == 1 && components.get(0).size() == vertexCount;
        check("deep cycle of " + vertexCount, correct, true);
    }

    private static List<List<Integer>> scc(DirectedGraph graph) {
        return new StronglyConnectedComponents().find(graph);
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
