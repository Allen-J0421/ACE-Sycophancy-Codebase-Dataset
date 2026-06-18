package matrixchain;

final class MatrixChainSolver {

    private MatrixChainSolver() {
        throw new AssertionError("No instances");
    }

    static MatrixChainResult optimize(MatrixDimensions dimensions) {
        int matrixCount = dimensions.matrixCount();
        int[] values = dimensions.values();
        long[][] minCost = new long[matrixCount][matrixCount];
        int[][] split = new int[matrixCount][matrixCount];

        for (int chainLength = 2; chainLength <= matrixCount; chainLength++) {
            for (int start = 0; start <= matrixCount - chainLength; start++) {
                int end = start + chainLength - 1;
                minCost[start][end] = Long.MAX_VALUE;

                for (int pivot = start; pivot < end; pivot++) {
                    long cost = Math.addExact(
                            Math.addExact(minCost[start][pivot], minCost[pivot + 1][end]),
                            multiplicationCost(values, start, pivot, end));
                    if (cost < minCost[start][end]) {
                        minCost[start][end] = cost;
                        split[start][end] = pivot;
                    }
                }
            }
        }

        return new MatrixChainResult(
                minCost[0][matrixCount - 1],
                buildParenthesization(split, 0, matrixCount - 1));
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
}
