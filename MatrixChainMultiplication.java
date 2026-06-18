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

        int[][] minimumCosts = buildMinimumCostTable(dimensions);
        return minimumCosts[0][lastDimensionIndex(dimensions)];
    }

    private static int[][] buildMinimumCostTable(int[] dimensions) {
        int dimensionCount = dimensions.length;
        int[][] minimumCosts = new int[dimensionCount][dimensionCount];

        for (int chainLength = 2; chainLength < dimensionCount; chainLength++) {
            fillCostsForChainLength(dimensions, minimumCosts, chainLength);
        }

        return minimumCosts;
    }

    private static void fillCostsForChainLength(
            int[] dimensions,
            int[][] minimumCosts,
            int chainLength) {
        for (int start = 0; start < dimensions.length - chainLength; start++) {
            int end = start + chainLength;
            minimumCosts[start][end] = minimumCostForRange(dimensions, minimumCosts, start, end);
        }
    }

    private static int minimumCostForRange(
            int[] dimensions,
            int[][] minimumCosts,
            int start,
            int end) {
        int minimumCost = Integer.MAX_VALUE;

        for (int split = start + 1; split < end; split++) {
            int cost = costForSplit(dimensions, minimumCosts, start, split, end);
            minimumCost = Math.min(minimumCost, cost);
        }

        return minimumCost;
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

    private static int lastDimensionIndex(int[] dimensions) {
        return dimensions.length - 1;
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
