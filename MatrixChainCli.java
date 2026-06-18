public final class MatrixChainCli {

    private static final MatrixDimensions DEFAULT_DIMENSIONS =
        MatrixDimensions.of(new int[] {2, 1, 3, 4});

    private MatrixChainCli() {
    }

    public static void main(String[] args) {
        MatrixDimensions dimensions = args.length == 0
            ? DEFAULT_DIMENSIONS
            : MatrixDimensions.parse(args);

        MatrixChainResult result = MatrixChainMultiplication.solve(dimensions);
        System.out.println(result.minimumCost());
    }
}
