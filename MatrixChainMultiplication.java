import java.util.Objects;

public final class MatrixChainMultiplication {

    private MatrixChainMultiplication() {
        throw new AssertionError("No instances");
    }

    public static int matrixMultiplication(int[] dimensions) {
        return Math.toIntExact(optimize(dimensions).minimumCost());
    }

    public static OptimizationResult optimize(int[] dimensions) {
        validateDimensions(dimensions);

        int matrixCount = dimensions.length - 1;
        long[][] minCost = new long[matrixCount][matrixCount];
        int[][] split = new int[matrixCount][matrixCount];

        for (int chainLength = 2; chainLength <= matrixCount; chainLength++) {
            for (int start = 0; start <= matrixCount - chainLength; start++) {
                int end = start + chainLength - 1;
                minCost[start][end] = Long.MAX_VALUE;

                for (int pivot = start; pivot < end; pivot++) {
                    long cost = Math.addExact(
                            Math.addExact(minCost[start][pivot], minCost[pivot + 1][end]),
                            multiplicationCost(dimensions, start, pivot, end));
                    if (cost < minCost[start][end]) {
                        minCost[start][end] = cost;
                        split[start][end] = pivot;
                    }
                }
            }
        }

        return new OptimizationResult(
                minCost[0][matrixCount - 1],
                buildParenthesization(split, 0, matrixCount - 1));
    }

    private static void validateDimensions(int[] dimensions) {
        Objects.requireNonNull(dimensions, "dimensions");
        if (dimensions.length < 2) {
            throw new IllegalArgumentException(
                    "At least two dimensions are required to describe one matrix");
        }
        for (int dimension : dimensions) {
            if (dimension <= 0) {
                throw new IllegalArgumentException("Matrix dimensions must be positive");
            }
        }
    }

    private static long multiplicationCost(int[] dimensions, int start, int pivot, int end) {
        long left = dimensions[start];
        long middle = dimensions[pivot + 1];
        long right = dimensions[end + 1];
        return Math.multiplyExact(Math.multiplyExact(left, middle), right);
    }

    private static String buildParenthesization(int[][] split, int start, int end) {
        if (start == end) {
            return "A" + (start + 1);
        }

        int pivot = split[start][end];
        return "("
                + buildParenthesization(split, start, pivot)
                + " * "
                + buildParenthesization(split, pivot + 1, end)
                + ")";
    }

    public static void main(String[] args) {
        int[] dimensions = { 2, 1, 3, 4 };
        System.out.println(matrixMultiplication(dimensions));
    }

    public static final class OptimizationResult {
        private final long minimumCost;
        private final String parenthesization;

        private OptimizationResult(long minimumCost, String parenthesization) {
            this.minimumCost = minimumCost;
            this.parenthesization = parenthesization;
        }

        public long minimumCost() {
            return minimumCost;
        }

        public String parenthesization() {
            return parenthesization;
        }
    }
}
