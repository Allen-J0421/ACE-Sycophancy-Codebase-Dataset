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
        private final OptimalOrderTables tables;

        private SolverState(MatrixDimensions dimensions) {
            this.dimensions = dimensions;
            this.dimensionCount = dimensions.size();
            this.matrixCount = dimensions.matrixCount();
            this.tables = new OptimalOrderTables(dimensionCount);
        }

        private void compute() {
            for (int chainLength = 2; chainLength < dimensionCount; chainLength++) {
                for (int start = 0; start < dimensionCount - chainLength; start++) {
                    int end = start + chainLength;
                    CostEvaluation costEvaluation = findMinimumCost(start, end);
                    tables.record(start, end, costEvaluation);
                }
            }
        }

        private MatrixChainResult toResult() {
            return new MatrixChainResult(
                tables.minimumCost(0, dimensionCount - 1),
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
            return tables.minimumCost(start, split)
                + tables.minimumCost(split, end)
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
                parenthesization.append(dimensions.matrixLabel(start));
                return;
            }

            int split = tables.bestSplit(start, end);
            parenthesization.append('(');
            appendParenthesization(parenthesization, start, split);
            parenthesization.append(" x ");
            appendParenthesization(parenthesization, split, end);
            parenthesization.append(')');
        }
    }

    private static final class OptimalOrderTables {
        private final int[][] minimumCosts;
        private final int[][] bestSplits;

        private OptimalOrderTables(int dimensionCount) {
            this.minimumCosts = new int[dimensionCount][dimensionCount];
            this.bestSplits = new int[dimensionCount][dimensionCount];
        }

        private void record(int start, int end, CostEvaluation costEvaluation) {
            minimumCosts[start][end] = costEvaluation.cost();
            bestSplits[start][end] = costEvaluation.split();
        }

        private int minimumCost(int start, int end) {
            return minimumCosts[start][end];
        }

        private int bestSplit(int start, int end) {
            return bestSplits[start][end];
        }
    }

    private record CostEvaluation(int cost, int split) {
    }
}
