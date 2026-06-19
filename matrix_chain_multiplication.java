/**
 * Solves the matrix chain multiplication problem using dynamic programming.
 * Finds the minimum number of scalar multiplications needed to multiply a chain of matrices.
 *
 * Time Complexity: O(n³) where n is the number of matrices
 * Space Complexity: O(n²) for the DP tables
 */
public class MatrixChainMultiplication {

    private static final int MIN_MATRICES = 2;

    /**
     * Result object containing the solution and metadata.
     */
    public static class Solution {
        private final int minCost;
        private final int[][] splits;
        private final long computationTimeMs;

        Solution(int minCost, int[][] splits, long computationTimeMs) {
            this.minCost = minCost;
            this.splits = splits;
            this.computationTimeMs = computationTimeMs;
        }

        public int getMinimumCost() {
            return minCost;
        }

        public String getOptimalParenthesization(int startIndex, int endIndex) {
            if (startIndex == endIndex) {
                return "M" + startIndex;
            }
            int splitPoint = splits[startIndex][endIndex];
            String left = getOptimalParenthesization(startIndex, splitPoint);
            String right = getOptimalParenthesization(splitPoint + 1, endIndex);
            return "(" + left + " × " + right + ")";
        }

        public long getComputationTimeMs() {
            return computationTimeMs;
        }

        @Override
        public String toString() {
            return String.format("Minimum multiplications: %d (computed in %dms)",
                    minCost, computationTimeMs);
        }
    }

    /**
     * Calculates the minimum number of scalar multiplications needed.
     *
     * @param dimensions array where dimensions[i] represents the row count of matrix i
     *                   and dimensions[i+1] represents the column count
     * @return Solution containing minimum cost and optimal parenthesization
     * @throws IllegalArgumentException if dimensions is null or has fewer than 2 elements
     */
    public static Solution solve(int[] dimensions) {
        long startTime = System.currentTimeMillis();
        validateInput(dimensions);

        int numMatrices = dimensions.length;
        int[][] cost = initializeCostTable(numMatrices);
        int[][] splits = initializeSplitTable(numMatrices);
        computeMinimalCosts(cost, splits, dimensions, numMatrices);

        long endTime = System.currentTimeMillis();
        return new Solution(cost[0][numMatrices - 1], splits, endTime - startTime);
    }

    private static void validateInput(int[] dimensions) {
        if (dimensions == null || dimensions.length < MIN_MATRICES) {
            throw new IllegalArgumentException("At least 2 matrices required");
        }
    }

    private static int[][] initializeCostTable(int numMatrices) {
        return new int[numMatrices][numMatrices];
    }

    private static int[][] initializeSplitTable(int numMatrices) {
        return new int[numMatrices][numMatrices];
    }

    private static void computeMinimalCosts(int[][] cost, int[][] splits,
                                            int[] dimensions, int numMatrices) {
        for (int chainLength = 2; chainLength < numMatrices; chainLength++) {
            for (int startIndex = 0; startIndex < numMatrices - chainLength; startIndex++) {
                int endIndex = startIndex + chainLength;
                cost[startIndex][endIndex] = Integer.MAX_VALUE;

                for (int splitPoint = startIndex + 1; splitPoint < endIndex; splitPoint++) {
                    int currentCost = calculateCost(cost, dimensions, startIndex, splitPoint, endIndex);
                    if (currentCost < cost[startIndex][endIndex]) {
                        cost[startIndex][endIndex] = currentCost;
                        splits[startIndex][endIndex] = splitPoint;
                    }
                }
            }
        }
    }

    private static int calculateCost(int[][] cost, int[] dimensions,
                                     int startIndex, int splitPoint, int endIndex) {
        return cost[startIndex][splitPoint]
             + cost[splitPoint][endIndex]
             + dimensions[startIndex] * dimensions[splitPoint] * dimensions[endIndex];
    }

    public static void main(String[] args) {
        int[] matrixDimensions = { 2, 1, 3, 4 };
        Solution solution = solve(matrixDimensions);
        System.out.println(solution);
        System.out.println("Optimal parenthesization: " + solution.getOptimalParenthesization(0, 2));
    }
}
