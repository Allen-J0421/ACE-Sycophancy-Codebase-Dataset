package matrixchain;

final class MatrixChainArguments {

    private static final MatrixDimensions DEFAULT_DIMENSIONS = MatrixDimensions.of(2, 1, 3, 4);

    private MatrixChainArguments() {
        throw new AssertionError("No instances");
    }

    static MatrixDimensions parse(String[] args) {
        if (args.length == 0) {
            return DEFAULT_DIMENSIONS;
        }

        if (args.length < 2) {
            throw new IllegalArgumentException(
                    "Expected at least two dimensions, for example: 10 20 30");
        }

        int[] dimensions = new int[args.length];
        for (int index = 0; index < args.length; index++) {
            dimensions[index] = parseDimension(args[index], index);
        }
        return new MatrixDimensions(dimensions);
    }

    static String usage() {
        return "Usage: java matrixchain.MatrixChainCli <d1> <d2> ... <dn>";
    }

    private static int parseDimension(String token, int index) {
        try {
            return Integer.parseInt(token);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "Invalid dimension at position " + (index + 1) + ": " + token, exception);
        }
    }
}
