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
                    Subchain subchain = new Subchain(start, start + chainLength);
                    CostEvaluation costEvaluation = findMinimumCost(subchain);
                    tables.record(subchain, costEvaluation);
                }
            }
        }

        private MatrixChainResult toResult() {
            Subchain rootSubchain = new Subchain(0, dimensionCount - 1);
            return new MatrixChainResult(
                tables.minimumCost(rootSubchain),
                buildParenthesization(rootSubchain)
            );
        }

        private CostEvaluation findMinimumCost(Subchain subchain) {
            int firstSplit = subchain.firstSplit();
            int bestCost = costForSplit(subchain, firstSplit);
            int bestSplit = firstSplit;

            for (int split = firstSplit + 1; split < subchain.end(); split++) {
                int cost = costForSplit(subchain, split);
                if (cost < bestCost) {
                    bestCost = cost;
                    bestSplit = split;
                }
            }

            return new CostEvaluation(bestCost, bestSplit);
        }

        private int costForSplit(Subchain subchain, int split) {
            return tables.minimumCost(subchain.leftOf(split))
                + tables.minimumCost(subchain.rightOf(split))
                + multiplicationCost(subchain, split);
        }

        private int multiplicationCost(Subchain subchain, int split) {
            return dimensions.valueAt(subchain.start())
                * dimensions.valueAt(split)
                * dimensions.valueAt(subchain.end());
        }

        private String buildParenthesization(Subchain rootSubchain) {
            StringBuilder parenthesization = new StringBuilder(matrixCount * 6);
            appendParenthesization(parenthesization, rootSubchain);
            return parenthesization.toString();
        }

        private void appendParenthesization(StringBuilder parenthesization, Subchain subchain) {
            if (subchain.isSingleMatrix()) {
                parenthesization.append(dimensions.matrixLabel(subchain.start()));
                return;
            }

            int split = tables.bestSplit(subchain);
            parenthesization.append('(');
            appendParenthesization(parenthesization, subchain.leftOf(split));
            parenthesization.append(" x ");
            appendParenthesization(parenthesization, subchain.rightOf(split));
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

        private void record(Subchain subchain, CostEvaluation costEvaluation) {
            minimumCosts[subchain.start()][subchain.end()] = costEvaluation.cost();
            bestSplits[subchain.start()][subchain.end()] = costEvaluation.split();
        }

        private int minimumCost(Subchain subchain) {
            return minimumCosts[subchain.start()][subchain.end()];
        }

        private int bestSplit(Subchain subchain) {
            return bestSplits[subchain.start()][subchain.end()];
        }
    }

    private record Subchain(int start, int end) {
        private int firstSplit() {
            return start + 1;
        }

        private boolean isSingleMatrix() {
            return end - start == 1;
        }

        private Subchain leftOf(int split) {
            return new Subchain(start, split);
        }

        private Subchain rightOf(int split) {
            return new Subchain(split, end);
        }
    }

    private record CostEvaluation(int cost, int split) {
    }
}
