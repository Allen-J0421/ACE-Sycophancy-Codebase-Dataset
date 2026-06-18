package matrixchain;

public final class MatrixChainCli {

    private MatrixChainCli() {
        throw new AssertionError("No instances");
    }

    public static void main(String[] args) {
        int[] dimensions = { 2, 1, 3, 4 };
        MatrixChainResult result = MatrixChainMultiplication.optimize(new MatrixDimensions(dimensions));
        System.out.println(result.minimumCost());
        System.out.println(result.parenthesization());
    }
}
