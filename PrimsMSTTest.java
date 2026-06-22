import java.util.List;

/**
 * Minimal, dependency-free test harness for the refactored Prim's MST.
 * Run with {@code java PrimsMSTTest}; exits non-zero if any check fails.
 */
public final class PrimsMSTTest {

    private static int failures = 0;

    public static void main(String[] args) {
        testClassicExampleEdges();
        testClassicExampleTotalWeight();
        testTabularOutputMatchesBaseline();
        testEmptyMatrixRejected();
        testNonSquareMatrixRejected();

        if (failures == 0) {
            System.out.println("All tests passed.");
        } else {
            System.out.println(failures + " test(s) failed.");
            System.exit(1);
        }
    }

    private static void testClassicExampleEdges() {
        MstResult result = new PrimsMST().computeMst(SampleGraphs.classicExample());
        List<Edge> edges = result.edges();
        check("edge count", 4, edges.size());
        check("edge 0", new Edge(0, 1, 2), edges.get(0));
        check("edge 1", new Edge(1, 2, 3), edges.get(1));
        check("edge 2", new Edge(0, 3, 6), edges.get(2));
        check("edge 3", new Edge(1, 4, 5), edges.get(3));
    }

    private static void testClassicExampleTotalWeight() {
        MstResult result = new PrimsMST().computeMst(SampleGraphs.classicExample());
        check("total weight", 16, result.totalWeight());
    }

    private static void testTabularOutputMatchesBaseline() {
        MstApplication app = new MstApplication(new PrimsMST(), new TabularMstFormatter());
        String expected = "Edge \tWeight\n0 - 1\t2\n1 - 2\t3\n0 - 3\t6\n1 - 4\t5";
        check("tabular output", expected, app.run(SampleGraphs.classicExample()));
    }

    private static void testEmptyMatrixRejected() {
        checkThrows("empty matrix", () -> WeightedGraph.fromAdjacencyMatrix(new int[0][0]));
    }

    private static void testNonSquareMatrixRejected() {
        checkThrows("non-square matrix",
                () -> WeightedGraph.fromAdjacencyMatrix(new int[][] { { 0, 1 }, { 1 } }));
    }

    private static void check(String name, Object expected, Object actual) {
        if (!expected.equals(actual)) {
            failures++;
            System.out.println("FAIL " + name + ": expected <" + expected + "> but was <" + actual + ">");
        }
    }

    private static void checkThrows(String name, Runnable action) {
        try {
            action.run();
            failures++;
            System.out.println("FAIL " + name + ": expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // pass
        }
    }
}
