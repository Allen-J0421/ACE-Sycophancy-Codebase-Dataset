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

    static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected " + expected + " but got " + actual);
        }
    }

    static void assertEquals(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected '" + expected + "' but got '" + actual + "'");
        }
    }

    static void testBasicCase() {
        // arr={2,1,3,4}: optimal is A1x(A2xA3) = 12+8 = 20
        assertEquals(20, MatrixChainMultiplication.matrixMultiplication(new int[]{2, 1, 3, 4}));
    }

    static void testSingleMatrix() {
        MatrixChainResult result = new MatrixChainSolver().solve(new MatrixDimensions[]{
            new MatrixDimensions(5, 10)
        });
        assertEquals(0, result.minCost);
        assertEquals("A1", result.parenthesization);
    }

    static void testTwoMatrices() {
        MatrixChainResult result = new MatrixChainSolver().solve(new MatrixDimensions[]{
            new MatrixDimensions(10, 30),
            new MatrixDimensions(30, 5)
        });
        assertEquals(1500, result.minCost);
        assertEquals("(A1 x A2)", result.parenthesization);
    }

    static void testFourMatrices() {
        // Classic example: arr={40,20,30,10,30}, answer=26000
        MatrixChainResult result = new MatrixChainSolver().solve(new MatrixDimensions[]{
            new MatrixDimensions(40, 20),
            new MatrixDimensions(20, 30),
            new MatrixDimensions(30, 10),
            new MatrixDimensions(10, 30)
        });
        assertEquals(26000, result.minCost);
    }

    static void testParenthesization() {
        // arr={2,1,3,4}: optimal split is at k=1, giving (A1 x (A2 x A3))
        MatrixChainResult result = new MatrixChainSolver().solve(new MatrixDimensions[]{
            new MatrixDimensions(2, 1),
            new MatrixDimensions(1, 3),
            new MatrixDimensions(3, 4)
        });
        assertEquals("(A1 x (A2 x A3))", result.parenthesization);
    }

    static void testInvalidDimensions() {
        assertThrows(IllegalArgumentException.class, () -> new MatrixDimensions(0, 5));
        assertThrows(IllegalArgumentException.class, () -> new MatrixDimensions(5, -1));
    }

    static void testEmptyInput() {
        assertThrows(IllegalArgumentException.class,
                () -> new MatrixChainSolver().solve(new MatrixDimensions[0]));
    }

    static void testArgumentParsing() {
        MatrixChainArguments args = MatrixChainArguments.parse(new String[]{"10", "30", "5"});
        assertEquals(2, args.matrices.length);
        assertEquals(10, args.matrices[0].rows);
        assertEquals(30, args.matrices[0].cols);
        assertEquals(30, args.matrices[1].rows);
        assertEquals(5, args.matrices[1].cols);
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
        testBasicCase();
        testSingleMatrix();
        testTwoMatrices();
        testFourMatrices();
        testParenthesization();
        testInvalidDimensions();
        testEmptyInput();
        testArgumentParsing();
        testArgumentParsingInvalid();
        System.out.println("All tests passed!");
    }
}
