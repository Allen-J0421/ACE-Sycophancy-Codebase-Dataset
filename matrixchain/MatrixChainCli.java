package matrixchain;

public final class MatrixChainCli {

    private MatrixChainCli() {
        throw new AssertionError("No instances");
    }

    public static void main(String[] args) {
        try {
            MatrixDimensions dimensions = args.length == 0
                    ? MatrixDimensions.of(2, 1, 3, 4)
                    : MatrixDimensions.parse(args);
            MatrixChainResult result = MatrixChainMultiplication.optimize(dimensions);
            System.out.println(result.minimumCost());
            System.out.println(result.parenthesization());
        } catch (IllegalArgumentException exception) {
            System.err.println(exception.getMessage());
            System.err.println("Usage: java matrixchain.MatrixChainCli <d1> <d2> ... <dn>");
        }
    }
}
