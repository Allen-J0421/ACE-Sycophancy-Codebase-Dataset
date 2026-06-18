package matrixchain;

public final class MatrixChainMultiplication {

    private MatrixChainMultiplication() {
        throw new AssertionError("No instances");
    }

    public static MatrixChainResult optimize(MatrixDimensions dimensions) {
        return MatrixChainSolver.optimize(dimensions);
    }

    public static int matrixMultiplication(MatrixDimensions dimensions) {
        return Math.toIntExact(optimize(dimensions).minimumCost());
    }
}
