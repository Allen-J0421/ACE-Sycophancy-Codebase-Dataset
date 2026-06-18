public final class MatrixChainMultiplication {

    private MatrixChainMultiplication() {
    }

    public static int matrixMultiplication(int[] dimensions) {
        return solve(dimensions).minimumCost();
    }

    public static int matrixMultiplication(MatrixDimensions dimensions) {
        return solve(dimensions).minimumCost();
    }

    public static String optimalParenthesization(int[] dimensions) {
        return solve(dimensions).optimalParenthesization();
    }

    public static String optimalParenthesization(MatrixDimensions dimensions) {
        return solve(dimensions).optimalParenthesization();
    }

    public static MatrixChainResult solve(int[] dimensions) {
        return solve(MatrixDimensions.of(dimensions));
    }

    public static MatrixChainResult solve(MatrixDimensions dimensions) {
        return MatrixChainSolver.solve(dimensions);
    }
}
