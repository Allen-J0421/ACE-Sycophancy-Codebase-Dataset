package matrixchain;

public class MatrixChainMultiplicationTest {

    @FunctionalInterface
    interface ThrowingRunnable {
        void run() throws Exception;
    }

    static void assertThrows(Class<? extends Throwable> expectedType, ThrowingRunnable runnable) {
        try {
            runnable.run();
            throw new AssertionError("Expected " + expectedType.getName() + " but nothing was thrown");
        } catch (Throwable t) {
            if (!expectedType.isInstance(t)) {
                throw new AssertionError(
                        "Expected " + expectedType.getName() + " but got " + t.getClass().getName(), t);
            }
        }
    }

    static void assertEquals(long expected, long actual) {
        if (expected != actual) {
            throw new AssertionError("Expected " + expected + " but got " + actual);
        }
    }

    static void assertEquals(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected '" + expected + "' but got '" + actual + "'");
        }
    }

    static void testSingleMatrix() {
        MatrixChainResult result = MatrixChainSolver.solve(new MatrixDimensions[]{
            new MatrixDimensions(5, 10)
        });
        assertEquals(0, result.minCost);
        assertEquals("A1", result.parenthesization);
    }

    static void testTwoMatrices() {
        MatrixChainResult result = MatrixChainSolver.solve(new MatrixDimensions[]{
            new MatrixDimensions(10, 30),
            new MatrixDimensions(30, 5)
        });
        assertEquals(1500, result.minCost);
        assertEquals("(A1 x A2)", result.parenthesization);
    }

    static void testThreeMatrices() {
        // 2x1, 1x3, 3x4: optimal is A1x(A2xA3) = cost 12+8 = 20
        MatrixChainResult result = MatrixChainSolver.solve(new MatrixDimensions[]{
            new MatrixDimensions(2, 1),
            new MatrixDimensions(1, 3),
            new MatrixDimensions(3, 4)
        });
        assertEquals(20, result.minCost);
        assertEquals("(A1 x (A2 x A3))", result.parenthesization);
    }

    static void testFourMatrices() {
        // Classic example: arr={40,20,30,10,30}, answer=26000
        MatrixChainResult result = MatrixChainSolver.solve(new MatrixDimensions[]{
            new MatrixDimensions(40, 20),
            new MatrixDimensions(20, 30),
            new MatrixDimensions(30, 10),
            new MatrixDimensions(10, 30)
        });
        assertEquals(26000, result.minCost);
    }

    static void testNullInput() {
        assertThrows(IllegalArgumentException.class, () -> MatrixChainSolver.solve(null));
    }

    static void testEmptyInput() {
        assertThrows(IllegalArgumentException.class,
                () -> MatrixChainSolver.solve(new MatrixDimensions[0]));
    }

    static void testInvalidDimensions() {
        assertThrows(IllegalArgumentException.class, () -> new MatrixDimensions(0, 5));
        assertThrows(IllegalArgumentException.class, () -> new MatrixDimensions(5, -1));
    }

    static void testIncompatibleChain() {
        assertThrows(IllegalArgumentException.class, () -> MatrixChainSolver.solve(new MatrixDimensions[]{
            new MatrixDimensions(2, 3),
            new MatrixDimensions(5, 4)  // 3 != 5: inner dimensions do not match
        }));
    }

    static void testArgumentParsing() {
        MatrixDimensions[] matrices = MatrixChainArguments.parse(new String[]{"10", "30", "5"}).matrices();
        assertEquals(2, matrices.length);
        assertEquals(10, matrices[0].rows);
        assertEquals(30, matrices[0].cols);
        assertEquals(30, matrices[1].rows);
        assertEquals(5, matrices[1].cols);
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
        testSingleMatrix();
        testTwoMatrices();
        testThreeMatrices();
        testFourMatrices();
        testNullInput();
        testEmptyInput();
        testInvalidDimensions();
        testIncompatibleChain();
        testArgumentParsing();
        testArgumentParsingInvalid();
        System.out.println("All tests passed!");
    }
}
