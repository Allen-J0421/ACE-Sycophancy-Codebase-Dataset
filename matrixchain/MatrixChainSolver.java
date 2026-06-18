package matrixchain;

final class MatrixChainSolver {

    MatrixChainResult solve(MatrixDimensions[] matrices) {
        int n = matrices.length;
        if (n == 0) throw new IllegalArgumentException("At least one matrix is required");
        if (n == 1) return new MatrixChainResult(0, "A1");

        int[] dims = buildDimsArray(matrices, n);
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

    private int[] buildDimsArray(MatrixDimensions[] matrices, int n) {
        int[] dims = new int[n + 1];
        dims[0] = matrices[0].rows;
        for (int i = 0; i < n; i++) {
            dims[i + 1] = matrices[i].cols;
        }
        return dims;
    }

    private String buildParenthesization(int[][] split, int i, int j) {
        if (i == j) return "A" + i;
        int k = split[i][j];
        return "(" + buildParenthesization(split, i, k)
                + " x " + buildParenthesization(split, k + 1, j) + ")";
    }
}
