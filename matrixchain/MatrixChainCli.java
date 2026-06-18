package matrixchain;

public final class MatrixChainCli {

    private MatrixChainCli() {
        throw new AssertionError("No instances");
    }

    public static void main(String[] args) {
        try {
            MatrixDimensions dimensions = parseDimensions(args);
            MatrixChainResult result = MatrixChainMultiplication.optimize(dimensions);
            System.out.println(result.minimumCost());
            System.out.println(result.parenthesization());
        } catch (IllegalArgumentException exception) {
            System.err.println(exception.getMessage());
            System.err.println("Usage: java matrixchain.MatrixChainCli <d1> <d2> ... <dn>");
        }
    }

    static MatrixDimensions parseDimensions(String[] args) {
        if (args.length == 0) {
            return new MatrixDimensions(new int[] { 2, 1, 3, 4 });
        }

        if (args.length < 2) {
            throw new IllegalArgumentException(
                    "Expected at least two dimensions, for example: 10 20 30");
        }

        int[] dimensions = new int[args.length];
        for (int index = 0; index < args.length; index++) {
            String token = args[index];
            try {
                dimensions[index] = Integer.parseInt(token);
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException(
                        "Invalid dimension at position " + (index + 1) + ": " + token, exception);
            }
        }
        return new MatrixDimensions(dimensions);
    }
}
