package matrixchain;

public final class MatrixChainMultiplication {

    private MatrixChainMultiplication() {
        throw new AssertionError("No instances");
    }

    public static int matrixMultiplication(int[] dimensions) {
        return Math.toIntExact(optimize(dimensions).minimumCost());
    }

    public static MatrixChainResult optimize(int[] dimensions) {
        return optimize(new MatrixDimensions(dimensions));
    }

    public static MatrixChainResult optimize(MatrixDimensions dimensions) {
        return MatrixChainSolver.optimize(dimensions);
    }
}
