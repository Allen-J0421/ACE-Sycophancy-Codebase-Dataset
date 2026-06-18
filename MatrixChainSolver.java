final class MatrixChainSolver {

    private MatrixChainSolver() {
    }

    static MatrixChainResult solve(int[] dimensions) {
        if (dimensions.length < 2) {
            return MatrixChainResult.empty();
        }

        int dimensionCount = dimensions.length;
        int[][] minimumCosts = new int[dimensionCount][dimensionCount];
        int[][] bestSplits = new int[dimensionCount][dimensionCount];

        for (int chainLength = 2; chainLength < dimensionCount; chainLength++) {
            for (int start = 0; start < dimensionCount - chainLength; start++) {
                int end = start + chainLength;
                CostEvaluation costEvaluation = findMinimumCost(
                    minimumCosts,
                    dimensions,
                    start,
                    end
                );
                minimumCosts[start][end] = costEvaluation.cost();
                bestSplits[start][end] = costEvaluation.split();
            }
        }

        return new MatrixChainResult(
            minimumCosts[0][dimensionCount - 1],
            bestSplits,
            dimensionCount - 1
        );
    }

    private static CostEvaluation findMinimumCost(
        int[][] minimumCosts,
        int[] dimensions,
        int start,
        int end
    ) {
        int firstSplit = start + 1;
        int bestCost = costForSplit(minimumCosts, dimensions, start, firstSplit, end);
        int bestSplit = firstSplit;

        for (int split = firstSplit + 1; split < end; split++) {
            int cost = costForSplit(minimumCosts, dimensions, start, split, end);
            if (cost < bestCost) {
                bestCost = cost;
                bestSplit = split;
            }
        }

        return new CostEvaluation(bestCost, bestSplit);
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

    private record CostEvaluation(int cost, int split) {
    }
}
