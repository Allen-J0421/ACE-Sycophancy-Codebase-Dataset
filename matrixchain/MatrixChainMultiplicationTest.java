package matrixchain;

final class MatrixChainMultiplicationTest {

    private MatrixChainMultiplicationTest() {
        throw new AssertionError("No instances");
    }

    public static void main(String[] args) {
        MatrixChainResult sample =
                MatrixChainMultiplication.optimize(new int[] { 2, 1, 3, 4 });
        expectEquals(20L, sample.minimumCost(), "sample chain cost");
        expectEquals("(A1 * (A2 * A3))", sample.parenthesization(), "sample chain order");

        expectEquals(20, MatrixChainMultiplication.matrixMultiplication(new int[] { 2, 1, 3, 4 }),
                "sample chain");
        expectEquals(0, MatrixChainMultiplication.matrixMultiplication(new int[] { 5, 10 }),
                "single matrix");
        expectThrows(NullPointerException.class,
                () -> MatrixChainMultiplication.matrixMultiplication(null),
                "null dimensions");
        expectThrows(IllegalArgumentException.class,
                () -> MatrixChainMultiplication.matrixMultiplication(new int[] { 2, 0, 3 }),
                "non-positive dimension");

        System.out.println("All tests passed.");
    }

    private static void expectEquals(long expected, long actual, String label) {
        if (expected != actual) {
            throw new AssertionError(label + ": expected " + expected + " but was " + actual);
        }
    }

    private static void expectEquals(String expected, String actual, String label) {
        if (!expected.equals(actual)) {
            throw new AssertionError(label + ": expected " + expected + " but was " + actual);
        }
    }

    private static void expectEquals(int expected, int actual, String label) {
        if (expected != actual) {
            throw new AssertionError(label + ": expected " + expected + " but was " + actual);
        }
    }

    private static void expectThrows(Class<? extends Throwable> expectedType, ThrowingRunnable action,
            String label) {
        try {
            action.run();
        } catch (Throwable thrown) {
            if (expectedType.isInstance(thrown)) {
                return;
            }
            throw new AssertionError(
                    label + ": expected " + expectedType.getSimpleName() + " but got "
                            + thrown.getClass().getSimpleName(),
                    thrown);
        }
        throw new AssertionError(label + ": expected " + expectedType.getSimpleName() + " but nothing was thrown");
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Throwable;
    }
}
