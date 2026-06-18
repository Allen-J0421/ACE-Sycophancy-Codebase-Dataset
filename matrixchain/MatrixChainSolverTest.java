package matrixchain;

public class MatrixChainSolverTest {

    @FunctionalInterface
    interface ThrowingRunnable {
        void run() throws Exception;
    }

    static void assertThrows(Class<? extends Throwable> expectedType, ThrowingRunnable runnable) {
        boolean threw = false;
        try {
            runnable.run();
        } catch (Throwable t) {
            threw = true;
            if (!expectedType.isInstance(t)) {
                throw new AssertionError(
                        "Expected " + expectedType.getName() + " but got " + t.getClass().getName(), t);
            }
        }
        if (!threw) {
            throw new AssertionError("Expected " + expectedType.getName() + " but nothing was thrown");
        }
    }

    static void assertEquals(Object expected, Object actual) {
        boolean equal = (expected == null) ? (actual == null) : expected.equals(actual);
        if (!equal) {
            throw new AssertionError("Expected:\n  " + expected + "\nbut got:\n  " + actual);
        }
    }

    static void assertTrue(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    static void assertFalse(boolean condition, String message) {
        if (condition) throw new AssertionError(message);
    }

    // --- MatrixDimensions ---

    static void testDimensionsValidation() {
        assertThrows(IllegalArgumentException.class, () -> new MatrixDimensions(0, 5));
        assertThrows(IllegalArgumentException.class, () -> new MatrixDimensions(5, -1));
    }

    static void testDimensionsEquality() {
        MatrixDimensions a = new MatrixDimensions(3, 4);
        MatrixDimensions b = new MatrixDimensions(3, 4);
        MatrixDimensions c = new MatrixDimensions(3, 5);
        assertEquals(a, b);
        assertFalse(a.equals(c), "Different dimensions should not be equal");
        assertTrue(a.hashCode() == b.hashCode(), "Equal dimensions should have equal hash codes");
    }

    static void testDimensionsCompatibility() {
        assertTrue(new MatrixDimensions(2, 3).isCompatibleWith(new MatrixDimensions(3, 4)),
                "Matching inner dimension should be compatible");
        assertFalse(new MatrixDimensions(2, 3).isCompatibleWith(new MatrixDimensions(5, 4)),
                "Mismatched inner dimension should not be compatible");
    }

    // --- MatrixChainResult ---

    static void testResultEquality() {
        MatrixChainResult a = new MatrixChainResult(42, "(A1 x A2)");
        MatrixChainResult b = new MatrixChainResult(42, "(A1 x A2)");
        MatrixChainResult c = new MatrixChainResult(99, "(A1 x A2)");
        assertEquals(a, b);
        assertFalse(a.equals(c), "Results with different costs should not be equal");
        assertTrue(a.hashCode() == b.hashCode(), "Equal results should have equal hash codes");
    }

    static void testResultToString() {
        assertEquals(
                "Minimum multiplications: 26000\nOptimal order: ((A1 x (A2 x A3)) x A4)",
                new MatrixChainResult(26000, "((A1 x (A2 x A3)) x A4)").toString());
    }

    // --- MatrixChainSolver ---

    static void testSingleMatrix() {
        assertEquals(new MatrixChainResult(0, "A1"),
                MatrixChainSolver.solve(new MatrixDimensions[]{new MatrixDimensions(5, 10)}));
    }

    static void testTwoMatrices() {
        assertEquals(new MatrixChainResult(1500, "(A1 x A2)"),
                MatrixChainSolver.solve(new MatrixDimensions[]{
                    new MatrixDimensions(10, 30),
                    new MatrixDimensions(30, 5)
                }));
    }

    static void testThreeMatrices() {
        // 2x1, 1x3, 3x4: optimal is A1x(A2xA3) = cost 12+8 = 20
        assertEquals(new MatrixChainResult(20, "(A1 x (A2 x A3))"),
                MatrixChainSolver.solve(new MatrixDimensions[]{
                    new MatrixDimensions(2, 1),
                    new MatrixDimensions(1, 3),
                    new MatrixDimensions(3, 4)
                }));
    }

    static void testFourMatrices() {
        // Classic example: arr={40,20,30,10,30}, answer=26000
        assertEquals(new MatrixChainResult(26000, "((A1 x (A2 x A3)) x A4)"),
                MatrixChainSolver.solve(new MatrixDimensions[]{
                    new MatrixDimensions(40, 20),
                    new MatrixDimensions(20, 30),
                    new MatrixDimensions(30, 10),
                    new MatrixDimensions(10, 30)
                }));
    }

    static void testNullArray() {
        assertThrows(IllegalArgumentException.class, () -> MatrixChainSolver.solve(null));
    }

    static void testNullElementInChain() {
        assertThrows(IllegalArgumentException.class, () -> MatrixChainSolver.solve(new MatrixDimensions[]{
            new MatrixDimensions(2, 3),
            null
        }));
    }

    static void testEmptyChain() {
        assertThrows(IllegalArgumentException.class,
                () -> MatrixChainSolver.solve(new MatrixDimensions[0]));
    }

    static void testIncompatibleChain() {
        assertThrows(IllegalArgumentException.class, () -> MatrixChainSolver.solve(new MatrixDimensions[]{
            new MatrixDimensions(2, 3),
            new MatrixDimensions(5, 4)  // 3 != 5
        }));
    }

    // --- MatrixChainArguments ---

    static void testArgumentParsing() {
        MatrixDimensions[] matrices = MatrixChainArguments.parse(new String[]{"10", "30", "5"}).matrices();
        assertEquals(2, matrices.length);
        assertEquals(new MatrixDimensions(10, 30), matrices[0]);
        assertEquals(new MatrixDimensions(30, 5), matrices[1]);
    }

    static void testArgumentsDefensiveCopy() {
        MatrixChainArguments args = MatrixChainArguments.parse(new String[]{"10", "30", "5"});
        MatrixDimensions[] copy = args.matrices();
        copy[0] = null;
        assertEquals(new MatrixDimensions(10, 30), args.matrices()[0]);
    }

    static void testArgumentParsingInvalid() {
        assertThrows(IllegalArgumentException.class,
                () -> MatrixChainArguments.parse(new String[]{"10"}));
        assertThrows(IllegalArgumentException.class,
                () -> MatrixChainArguments.parse(new String[]{"abc", "10"}));
        assertThrows(IllegalArgumentException.class,
                () -> MatrixChainArguments.parse(new String[]{"0", "10"}));
    }

    public static void main(String[] args) {
        testDimensionsValidation();
        testDimensionsEquality();
        testDimensionsCompatibility();
        testResultEquality();
        testResultToString();
        testSingleMatrix();
        testTwoMatrices();
        testThreeMatrices();
        testFourMatrices();
        testNullArray();
        testNullElementInChain();
        testEmptyChain();
        testIncompatibleChain();
        testArgumentParsing();
        testArgumentsDefensiveCopy();
        testArgumentParsingInvalid();
        System.out.println("All tests passed!");
    }
}
