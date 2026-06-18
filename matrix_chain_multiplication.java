import java.util.Objects;

final class MatrixChainMultiplication {

    private MatrixChainMultiplication() {
        throw new AssertionError("No instances");
    }

    static int matrixMultiplication(int[] dimensions) {
        validateDimensions(dimensions);

        int matrixCount = dimensions.length - 1;
        int[][] minCost = new int[matrixCount][matrixCount];

        // minCost[i][j] stores the minimum scalar multiplications needed to
        // multiply the chain from matrix i through matrix j, inclusive.
        for (int chainLength = 2; chainLength <= matrixCount; chainLength++) {
            for (int start = 0; start <= matrixCount - chainLength; start++) {
                int end = start + chainLength - 1;
                minCost[start][end] = Integer.MAX_VALUE;

                for (int split = start; split < end; split++) {
                    int cost = minCost[start][split]
                            + minCost[split + 1][end]
                            + dimensions[start] * dimensions[split + 1] * dimensions[end + 1];
                    if (cost < minCost[start][end]) {
                        minCost[start][end] = cost;
                    }
                }
            }
        }

        return minCost[0][matrixCount - 1];
    }

    private static void validateDimensions(int[] dimensions) {
        Objects.requireNonNull(dimensions, "dimensions");
        if (dimensions.length < 2) {
            throw new IllegalArgumentException(
                    "At least two dimensions are required to describe one matrix");
        }
    }

    public static void main(String[] args) {
        int[] dimensions = { 2, 1, 3, 4 };
        System.out.println(matrixMultiplication(dimensions));
    }
}
