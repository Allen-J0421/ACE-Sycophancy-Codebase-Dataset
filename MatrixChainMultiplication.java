public final class MatrixChainMultiplication {

    private MatrixChainMultiplication() {
        throw new AssertionError("No instances");
    }

    public static int matrixMultiplication(int[] dimensions) {
        return Math.toIntExact(optimize(dimensions).minimumCost());
    }

    public static MatrixChainResult optimize(int[] dimensions) {
        return MatrixChainSolver.optimize(dimensions);
    }
}
