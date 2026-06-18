final class MatrixChainMultiplication {

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
        int matrixCount = dimensions.length;
        int[][] minimumCosts = new int[matrixCount][matrixCount];

        for (int chainLength = 2; chainLength < matrixCount; chainLength++) {
            for (int start = 0; start < matrixCount - chainLength; start++) {
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
        int bestCost = Integer.MAX_VALUE;

        for (int split = start + 1; split < end; split++) {
            int cost = minimumCosts[start][split]
                + minimumCosts[split][end]
                + multiplicationCost(dimensions, start, split, end);
            bestCost = Math.min(bestCost, cost);
        }

        return bestCost == Integer.MAX_VALUE ? 0 : bestCost;
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
        int[] dimensions = {2, 1, 3, 4};
        System.out.println(matrixMultiplication(dimensions));
    }
}
