package matrixchain;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

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
        expectEquals(3, MatrixDimensions.of(2, 1, 3, 4).matrixCount(),
                "cli default sample chain");
        expectEquals(3, MatrixDimensions.parse("10", "20", "30", "40").matrixCount(),
                "cli parsed chain");
        expectThrows(IllegalArgumentException.class,
                () -> MatrixDimensions.parse("10"),
                "cli too few dimensions");
        expectThrows(IllegalArgumentException.class,
                () -> MatrixDimensions.parse("10", "x"),
                "cli invalid number");
        expectEquals(
                MatrixDimensions.of(2, 1, 3, 4),
                MatrixDimensions.of(2, 1, 3, 4),
                "dimension equality");
        expectEquals(
                "Usage: java matrixchain.MatrixChainCli <d1> <d2> ... <dn>",
                extractUsageLine(captureCliError(new String[] { "10", "x" })),
                "cli usage");
        expectEquals(
                "20" + System.lineSeparator() + "(A1 * (A2 * A3))",
                captureCliOutput(new String[0]),
                "cli default output");

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

    private static String captureCliOutput(String[] args) {
        return captureStreams(args, false);
    }

    private static String captureCliError(String[] args) {
        return captureStreams(args, true);
    }

    private static String extractUsageLine(String output) {
        String[] lines = output.split("\\R");
        return lines[lines.length - 1];
    }

    private static String captureStreams(String[] args, boolean errorOnly) {
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
        ByteArrayOutputStream errBuffer = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(outBuffer));
            System.setErr(new PrintStream(errBuffer));
            MatrixChainCli.main(args);
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
        String output = errorOnly ? errBuffer.toString() : outBuffer.toString();
        return output.trim();
    }
}
