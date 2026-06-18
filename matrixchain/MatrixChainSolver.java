package matrixchain;

final class MatrixChainSolver {

    private MatrixChainSolver() {}

    static MatrixChainResult solve(MatrixDimensions[] matrices) {
        int n = matrices.length;
        if (n == 0) throw new IllegalArgumentException("At least one matrix is required");
        if (n == 1) return new MatrixChainResult(0, "A1");

        validateChain(matrices);

        int[] dims = buildDimsArray(matrices);
        int[][] dp = new int[n + 1][n + 1];
        int[][] split = new int[n + 1][n + 1];

        for (int len = 2; len <= n; len++) {
            for (int i = 1; i <= n - len + 1; i++) {
                int j = i + len - 1;
                dp[i][j] = Integer.MAX_VALUE;
                for (int k = i; k < j; k++) {
                    int cost = dp[i][k] + dp[k + 1][j] + dims[i - 1] * dims[k] * dims[j];
                    if (cost < dp[i][j]) {
                        dp[i][j] = cost;
                        split[i][j] = k;
                    }
                }
            }
        }

        return new MatrixChainResult(dp[1][n], buildParenthesization(split, 1, n));
    }

    private static void validateChain(MatrixDimensions[] matrices) {
        for (int i = 0; i < matrices.length - 1; i++) {
            if (!matrices[i].isCompatibleWith(matrices[i + 1])) {
                throw new IllegalArgumentException(
                        "Matrix " + (i + 1) + " (" + matrices[i] + ") is incompatible with matrix "
                        + (i + 2) + " (" + matrices[i + 1] + "): inner dimensions must match");
            }
        }
    }

    private static int[] buildDimsArray(MatrixDimensions[] matrices) {
        int n = matrices.length;
        int[] dims = new int[n + 1];
        dims[0] = matrices[0].rows;
        for (int i = 0; i < n; i++) {
            dims[i + 1] = matrices[i].cols;
        }
        return dims;
    }

    private static String buildParenthesization(int[][] split, int i, int j) {
        if (i == j) return "A" + i;
        int k = split[i][j];
        return "(" + buildParenthesization(split, i, k)
                + " x " + buildParenthesization(split, k + 1, j) + ")";
    }
}
