final class MatrixChainSolver {

    private MatrixChainSolver() {
    }

    static MatrixChainResult solve(int[] dimensions) {
        if (dimensions.length < 2) {
            return MatrixChainResult.empty();
        }

        SolverState solverState = new SolverState(dimensions);
        solverState.compute();
        return solverState.toResult();
    }

    private static final class SolverState {
        private final int[] dimensions;
        private final int[][] minimumCosts;
        private final int[][] bestSplits;

        private SolverState(int[] dimensions) {
            this.dimensions = dimensions;
            this.minimumCosts = new int[dimensions.length][dimensions.length];
            this.bestSplits = new int[dimensions.length][dimensions.length];
        }

        private void compute() {
            for (int chainLength = 2; chainLength < dimensions.length; chainLength++) {
                for (int start = 0; start < dimensions.length - chainLength; start++) {
                    int end = start + chainLength;
                    CostEvaluation costEvaluation = findMinimumCost(start, end);
                    minimumCosts[start][end] = costEvaluation.cost();
                    bestSplits[start][end] = costEvaluation.split();
                }
            }
        }

        private MatrixChainResult toResult() {
            return new MatrixChainResult(
                minimumCosts[0][dimensions.length - 1],
                buildParenthesization(0, dimensions.length - 1)
            );
        }

        private CostEvaluation findMinimumCost(int start, int end) {
            int firstSplit = start + 1;
            int bestCost = costForSplit(start, firstSplit, end);
            int bestSplit = firstSplit;

            for (int split = firstSplit + 1; split < end; split++) {
                int cost = costForSplit(start, split, end);
                if (cost < bestCost) {
                    bestCost = cost;
                    bestSplit = split;
                }
            }

            return new CostEvaluation(bestCost, bestSplit);
        }

        private int costForSplit(int start, int split, int end) {
            return minimumCosts[start][split]
                + minimumCosts[split][end]
                + multiplicationCost(start, split, end);
        }

        private int multiplicationCost(int start, int split, int end) {
            return dimensions[start] * dimensions[split] * dimensions[end];
        }

        private String buildParenthesization(int start, int end) {
            if (end - start == 1) {
                return "A" + (start + 1);
            }

            int split = bestSplits[start][end];
            return "("
                + buildParenthesization(start, split)
                + " x "
                + buildParenthesization(split, end)
                + ")";
        }
    }

    private record CostEvaluation(int cost, int split) {
    }
}
