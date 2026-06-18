public final class MatrixChainMultiplication {

    private static final int[] DEFAULT_DIMENSIONS = {2, 1, 3, 4};

    private MatrixChainMultiplication() {
    }

    public static int matrixMultiplication(int[] dimensions) {
        return solve(dimensions).minimumCost();
    }

    public static String optimalParenthesization(int[] dimensions) {
        return solve(dimensions).optimalParenthesization();
    }

    public static MatrixChainResult solve(int[] dimensions) {
        validateDimensions(dimensions);
        return MatrixChainSolver.solve(dimensions);
    }

    private static void validateDimensions(int[] dimensions) {
        if (dimensions == null) {
            throw new IllegalArgumentException("Matrix dimensions cannot be null.");
        }

        for (int dimension : dimensions) {
            if (dimension <= 0) {
                throw new IllegalArgumentException("Matrix dimensions must be positive.");
            }
        }
    }

    public static void main(String[] args) {
        int[] dimensions = args.length == 0 ? DEFAULT_DIMENSIONS : parseDimensions(args);
        System.out.println(matrixMultiplication(dimensions));
    }

    private static int[] parseDimensions(String[] args) {
        int[] dimensions = new int[args.length];

        for (int index = 0; index < args.length; index++) {
            dimensions[index] = Integer.parseInt(args[index]);
        }

        return dimensions;
    }
}
