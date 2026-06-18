package matrixchain;

final class MatrixChainMultiplicationTest {

    private MatrixChainMultiplicationTest() {
        throw new AssertionError("No instances");
    }

    public static void main(String[] args) {
        MatrixDimensions dimensions = MatrixDimensions.of(2, 1, 3, 4);
        MatrixChainResult sample = MatrixChainMultiplication.optimize(dimensions);
        expectEquals(20L, sample.minimumCost(), "sample chain cost");
        expectEquals("(A1 * (A2 * A3))", sample.parenthesization(), "sample chain order");

        expectEquals(20, MatrixChainMultiplication.matrixMultiplication(MatrixDimensions.of(2, 1, 3, 4)),
                "sample chain");
        expectEquals(0, MatrixChainMultiplication.matrixMultiplication(MatrixDimensions.of(5, 10)),
                "single matrix");
        expectThrows(NullPointerException.class,
                () -> MatrixChainMultiplication.matrixMultiplication(null),
                "null dimensions");
        expectThrows(IllegalArgumentException.class,
                () -> MatrixChainMultiplication.matrixMultiplication(MatrixDimensions.of(2, 0, 3)),
                "non-positive dimension");
        expectThrows(IllegalArgumentException.class,
                () -> MatrixDimensions.of(5),
                "missing chain");
        expectEquals(3, MatrixChainCli.parseDimensions(new String[0]).matrixCount(),
                "cli default sample chain");
        expectEquals(3, MatrixChainCli.parseDimensions(new String[] { "10", "20", "30", "40" }).matrixCount(),
                "cli parsed chain");
        expectThrows(IllegalArgumentException.class,
                () -> MatrixChainCli.parseDimensions(new String[] { "10" }),
                "cli too few dimensions");
        expectThrows(IllegalArgumentException.class,
                () -> MatrixChainCli.parseDimensions(new String[] { "10", "x" }),
                "cli invalid number");
        expectEquals(
                MatrixDimensions.of(2, 1, 3, 4),
                MatrixDimensions.of(2, 1, 3, 4),
                "dimension equality");

        int[] copied = dimensions.values();
        copied[0] = 99;
        expectEquals(2, dimensions.values()[0], "defensive copy");

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

    private static void expectEquals(Object expected, Object actual, String label) {
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
