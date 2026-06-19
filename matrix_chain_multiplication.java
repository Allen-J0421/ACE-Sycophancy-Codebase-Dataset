/**
 * Factory and entry point for matrix chain multiplication solving.
 */
public class MatrixChainMultiplication {

    public static Solver createSolver(int[] dimensions) {
        return new DynamicProgrammingSolver(dimensions);
    }

    /**
     * Algorithm abstraction for solving matrix chain multiplication.
     */
    public interface Solver {
        Solution solve();
    }

    /**
     * Immutable result object containing the solution and metadata.
     */
    public static class Solution {
        private final int minimumCost;
        private final int[][] splitPoints;
        private final long computationTimeMs;
        private final int matrixCount;

        Solution(int minimumCost, int[][] splitPoints, long computationTimeMs, int matrixCount) {
            this.minimumCost = minimumCost;
            this.splitPoints = splitPoints;
            this.computationTimeMs = computationTimeMs;
            this.matrixCount = matrixCount;
        }

        public int getMinimumCost() {
            return minimumCost;
        }

        public long getComputationTimeMs() {
            return computationTimeMs;
        }

        public String getOptimalParenthesization() {
            return buildParenthesization(0, matrixCount - 2);
        }

        private String buildParenthesization(int start, int end) {
            if (start == end) {
                return "M" + start;
            }
            int split = splitPoints[start][end];
            String left = buildParenthesization(start, split);
            String right = buildParenthesization(split + 1, end);
            return "(" + left + " × " + right + ")";
        }

        @Override
        public String toString() {
            return String.format("Minimum multiplications: %d (computed in %dms), Optimal order: %s",
                    minimumCost, computationTimeMs, getOptimalParenthesization());
        }
    }

    /**
     * Bottom-up dynamic programming solver for matrix chain multiplication.
     * Time Complexity: O(n³) where n is the number of matrices
     * Space Complexity: O(n²) for the DP tables
     */
    static class DynamicProgrammingSolver implements Solver {
        private static final int MIN_MATRICES = 2;
        private final int[] dimensions;
        private final int numMatrices;
        private final int[][] costTable;
        private final int[][] splitTable;

        DynamicProgrammingSolver(int[] dimensions) {
            this.dimensions = validateAndStore(dimensions);
            this.numMatrices = dimensions.length;
            this.costTable = new int[numMatrices][numMatrices];
            this.splitTable = new int[numMatrices][numMatrices];
        }

        @Override
        public Solution solve() {
            long startTime = System.currentTimeMillis();
            computeOptimalCosts();
            long endTime = System.currentTimeMillis();

            return new Solution(costTable[0][numMatrices - 1], splitTable,
                    endTime - startTime, numMatrices);
        }

        private int[] validateAndStore(int[] dimensions) {
            if (dimensions == null || dimensions.length < MIN_MATRICES) {
                throw new IllegalArgumentException("At least 2 matrices required");
            }
            return dimensions;
        }

        private void computeOptimalCosts() {
            for (int chainLength = 2; chainLength < numMatrices; chainLength++) {
                for (int start = 0; start < numMatrices - chainLength; start++) {
                    int end = start + chainLength;
                    costTable[start][end] = Integer.MAX_VALUE;

                    for (int split = start + 1; split < end; split++) {
                        int cost = computeCostForSplit(start, split, end);
                        if (cost < costTable[start][end]) {
                            costTable[start][end] = cost;
                            splitTable[start][end] = split;
                        }
                    }
                }
            }
        }

        private int computeCostForSplit(int start, int split, int end) {
            return costTable[start][split]
                 + costTable[split][end]
                 + dimensions[start] * dimensions[split] * dimensions[end];
        }
    }

    public static void main(String[] args) {
        int[] matrixDimensions = { 2, 1, 3, 4 };
        Solution solution = createSolver(matrixDimensions).solve();
        System.out.println(solution);
    }
}
