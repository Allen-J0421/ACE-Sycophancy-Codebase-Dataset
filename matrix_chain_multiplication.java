public class MatrixChainMultiplication {

    private static final int MIN_MATRICES = 2;

    public static int matrixMultiplication(int[] dimensions) {
        if (dimensions == null || dimensions.length < MIN_MATRICES) {
            throw new IllegalArgumentException("At least 2 matrices required");
        }

        int numMatrices = dimensions.length;
        int[][] cost = new int[numMatrices][numMatrices];

        for (int chainLength = 2; chainLength < numMatrices; chainLength++) {
            for (int startIndex = 0; startIndex < numMatrices - chainLength; startIndex++) {
                int endIndex = startIndex + chainLength;
                cost[startIndex][endIndex] = Integer.MAX_VALUE;

                for (int splitPoint = startIndex + 1; splitPoint < endIndex; splitPoint++) {
                    int currentCost = cost[startIndex][splitPoint]
                                    + cost[splitPoint][endIndex]
                                    + dimensions[startIndex] * dimensions[splitPoint] * dimensions[endIndex];
                    cost[startIndex][endIndex] = Math.min(cost[startIndex][endIndex], currentCost);
                }
            }
        }

        return cost[0][numMatrices - 1];
    }

    public static void main(String[] args) {
        int[] matrixDimensions = { 2, 1, 3, 4 };
        int minMultiplications = matrixMultiplication(matrixDimensions);
        System.out.println("Minimum number of multiplications: " + minMultiplications);
    }
}
