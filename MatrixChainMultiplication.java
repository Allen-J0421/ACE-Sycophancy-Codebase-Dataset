public final class MatrixChainMultiplication {

    private static final int[] SAMPLE_DIMENSIONS = { 2, 1, 3, 4 };
    private static final int MINIMUM_DIMENSION_COUNT = 2;

    private MatrixChainMultiplication() {
    }

    static int matrixMultiplication(int[] dimensions) {
        return minimumMultiplicationCost(dimensions);
    }

    public static int minimumMultiplicationCost(int[] dimensions) {
        validateDimensions(dimensions);

        int dimensionCount = dimensions.length;
        int[][] minimumCosts = new int[dimensionCount][dimensionCount];

        for (int chainLength = 2; chainLength < dimensionCount; chainLength++) {
            for (int start = 0; start < dimensionCount - chainLength; start++) {
                int end = start + chainLength;
                minimumCosts[start][end] = Integer.MAX_VALUE;

                for (int split = start + 1; split < end; split++) {
                    int cost = costForSplit(dimensions, minimumCosts, start, split, end);
                    if (cost < minimumCosts[start][end]) {
                        minimumCosts[start][end] = cost;
                    }
                }
            }
        }

        return minimumCosts[0][dimensionCount - 1];
    }

    private static void validateDimensions(int[] dimensions) {
        if (dimensions == null) {
            throw new IllegalArgumentException("Dimensions must not be null.");
        }
        if (dimensions.length < MINIMUM_DIMENSION_COUNT) {
            throw new IllegalArgumentException("At least two dimensions are required.");
        }

        for (int dimension : dimensions) {
            if (dimension <= 0) {
                throw new IllegalArgumentException("Dimensions must be positive.");
            }
        }
    }

    private static int costForSplit(
            int[] dimensions,
            int[][] minimumCosts,
            int start,
            int split,
            int end) {
        return minimumCosts[start][split]
                + minimumCosts[split][end]
                + dimensions[start] * dimensions[split] * dimensions[end];
    }

    public static void main(String[] args) {
        System.out.println(minimumMultiplicationCost(SAMPLE_DIMENSIONS));
    }
}
