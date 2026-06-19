/**
 * Solves the matrix chain multiplication problem using dynamic programming.
 * Finds the minimum number of scalar multiplications needed to multiply a chain of matrices.
 *
 * Time Complexity: O(n³) where n is the number of matrices
 * Space Complexity: O(n²) for the DP table
 */
public class MatrixChainMultiplication {

    private static final int MIN_MATRICES = 2;

    /**
     * Calculates the minimum number of scalar multiplications needed.
     *
     * @param dimensions array where dimensions[i] represents the row count of matrix i
     *                   and dimensions[i+1] represents the column count
     * @return minimum number of scalar multiplications
     * @throws IllegalArgumentException if dimensions is null or has fewer than 2 elements
     */
    public static int matrixMultiplication(int[] dimensions) {
        validateInput(dimensions);

        int numMatrices = dimensions.length;
        int[][] cost = initializeCostTable(numMatrices);
        computeMinimalCosts(cost, dimensions, numMatrices);

        return cost[0][numMatrices - 1];
    }

    private static void validateInput(int[] dimensions) {
        if (dimensions == null || dimensions.length < MIN_MATRICES) {
            throw new IllegalArgumentException("At least 2 matrices required");
        }
    }

    private static int[][] initializeCostTable(int numMatrices) {
        return new int[numMatrices][numMatrices];
    }

    private static void computeMinimalCosts(int[][] cost, int[] dimensions, int numMatrices) {
        for (int chainLength = 2; chainLength < numMatrices; chainLength++) {
            for (int startIndex = 0; startIndex < numMatrices - chainLength; startIndex++) {
                int endIndex = startIndex + chainLength;
                cost[startIndex][endIndex] = Integer.MAX_VALUE;

                for (int splitPoint = startIndex + 1; splitPoint < endIndex; splitPoint++) {
                    int currentCost = calculateCost(cost, dimensions, startIndex, splitPoint, endIndex);
                    cost[startIndex][endIndex] = Math.min(cost[startIndex][endIndex], currentCost);
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
        int minMultiplications = matrixMultiplication(matrixDimensions);
        System.out.println("Minimum number of multiplications: " + minMultiplications);
    }
}
