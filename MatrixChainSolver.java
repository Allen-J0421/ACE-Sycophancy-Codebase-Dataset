final class MatrixChainSolver {

    private MatrixChainSolver() {
    }

    static MatrixChainResult solve(MatrixDimensions dimensions) {
        if (dimensions.isTrivialChain()) {
            return MatrixChainResult.empty();
        }

        SolverState solverState = new SolverState(dimensions);
        solverState.compute();
        return solverState.toResult();
    }

    private static final class SolverState {
        private final MatrixDimensions dimensions;
        private final int dimensionCount;
        private final int matrixCount;
        private final int[][] minimumCosts;
        private final int[][] bestSplits;

        private SolverState(MatrixDimensions dimensions) {
            this.dimensions = dimensions;
            this.dimensionCount = dimensions.size();
            this.matrixCount = dimensions.matrixCount();
            this.minimumCosts = new int[dimensionCount][dimensionCount];
            this.bestSplits = new int[dimensionCount][dimensionCount];
        }

        private void compute() {
            for (int chainLength = 2; chainLength < dimensionCount; chainLength++) {
                for (int start = 0; start < dimensionCount - chainLength; start++) {
                    int end = start + chainLength;
                    CostEvaluation costEvaluation = findMinimumCost(start, end);
                    minimumCosts[start][end] = costEvaluation.cost();
                    bestSplits[start][end] = costEvaluation.split();
                }
            }
        }

        private MatrixChainResult toResult() {
            return new MatrixChainResult(
                minimumCosts[0][dimensionCount - 1],
                buildParenthesization()
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
            return dimensions.valueAt(start)
                * dimensions.valueAt(split)
                * dimensions.valueAt(end);
        }

        private String buildParenthesization() {
            StringBuilder parenthesization = new StringBuilder(matrixCount * 6);
            appendParenthesization(parenthesization, 0, dimensionCount - 1);
            return parenthesization.toString();
        }

        private void appendParenthesization(StringBuilder parenthesization, int start, int end) {
            if (end - start == 1) {
                parenthesization.append('A').append(start + 1);
                return;
            }

            int split = bestSplits[start][end];
            parenthesization.append('(');
            appendParenthesization(parenthesization, start, split);
            parenthesization.append(" x ");
            appendParenthesization(parenthesization, split, end);
            parenthesization.append(')');
        }
    }

    private record CostEvaluation(int cost, int split) {
    }
}
