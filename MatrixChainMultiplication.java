public final class MatrixChainMultiplication {

    private static final int[] DEFAULT_DIMENSIONS = {2, 1, 3, 4};

    private MatrixChainMultiplication() {
    }

    static int matrixMultiplication(int[] dimensions) {
        validateDimensions(dimensions);

        if (dimensions.length < 2) {
            return 0;
        }

        int[][] minimumCosts = buildMinimumCostTable(dimensions);
        return minimumCosts[0][dimensions.length - 1];
    }

    private static int[][] buildMinimumCostTable(int[] dimensions) {
        int dimensionCount = dimensions.length;
        int[][] minimumCosts = new int[dimensionCount][dimensionCount];

        for (int chainLength = 2; chainLength < dimensionCount; chainLength++) {
            for (int start = 0; start < dimensionCount - chainLength; start++) {
                int end = start + chainLength;
                minimumCosts[start][end] = findMinimumCost(
                    minimumCosts,
                    dimensions,
                    start,
                    end
                );
            }
        }

        return minimumCosts;
    }

    private static int findMinimumCost(
        int[][] minimumCosts,
        int[] dimensions,
        int start,
        int end
    ) {
        int firstSplit = start + 1;
        int bestCost = costForSplit(minimumCosts, dimensions, start, firstSplit, end);

        for (int split = firstSplit + 1; split < end; split++) {
            int cost = costForSplit(minimumCosts, dimensions, start, split, end);
            bestCost = Math.min(bestCost, cost);
        }

        return bestCost;
    }

    private static int costForSplit(
        int[][] minimumCosts,
        int[] dimensions,
        int start,
        int split,
        int end
    ) {
        return minimumCosts[start][split]
            + minimumCosts[split][end]
            + multiplicationCost(dimensions, start, split, end);
    }

    private static int multiplicationCost(
        int[] dimensions,
        int start,
        int split,
        int end
    ) {
        return dimensions[start] * dimensions[split] * dimensions[end];
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
