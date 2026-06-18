package matrixchain;

final class MatrixChainMultiplication {

    static int matrixMultiplication(int[] arr) {
        if (arr.length < 2) throw new IllegalArgumentException("Need at least 2 values");
        MatrixDimensions[] matrices = new MatrixDimensions[arr.length - 1];
        for (int i = 0; i < matrices.length; i++) {
            matrices[i] = new MatrixDimensions(arr[i], arr[i + 1]);
        }
        return new MatrixChainSolver().solve(matrices).minCost;
    }
}
