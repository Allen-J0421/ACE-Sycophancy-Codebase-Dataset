package matrixchain;

final class MatrixChainSolver {

    private MatrixChainSolver() {}

    static MatrixChainResult solve(MatrixDimensions[] matrices) {
        if (matrices == null) throw new IllegalArgumentException("matrices must not be null");
        int n = matrices.length;
        if (n == 0) throw new IllegalArgumentException("At least one matrix is required");
        for (MatrixDimensions m : matrices) {
            if (m == null) throw new IllegalArgumentException("Matrix chain must not contain null elements");
        }

        int[] dims = buildValidatedDimsArray(matrices);
        long[][] dp = new long[n + 1][n + 1];
        int[][] split = new int[n + 1][n + 1];

        for (int len = 2; len <= n; len++) {
            for (int i = 1; i <= n - len + 1; i++) {
                int j = i + len - 1;
                dp[i][j] = Long.MAX_VALUE;
                for (int k = i; k < j; k++) {
                    long cost = dp[i][k] + dp[k + 1][j] + (long) dims[i - 1] * dims[k] * dims[j];
                    if (cost < dp[i][j]) {
                        dp[i][j] = cost;
                        split[i][j] = k;
                    }
                }
            }
        }

        return new MatrixChainResult(dp[1][n], buildParenthesization(split, 1, n));
    }

    private static int[] buildValidatedDimsArray(MatrixDimensions[] matrices) {
        int n = matrices.length;
        int[] dims = new int[n + 1];
        dims[0] = matrices[0].rows;
        for (int i = 0; i < n; i++) {
            dims[i + 1] = matrices[i].cols;
            if (i < n - 1 && !matrices[i].isCompatibleWith(matrices[i + 1])) {
                throw new IllegalArgumentException(
                        "Matrix " + (i + 1) + " (" + matrices[i] + ") is incompatible with matrix "
                        + (i + 2) + " (" + matrices[i + 1] + "): inner dimensions must match");
            }
        }
        return dims;
    }

    private static String buildParenthesization(int[][] split, int i, int j) {
        StringBuilder sb = new StringBuilder();
        appendParenthesization(sb, split, i, j);
        return sb.toString();
    }

    private static void appendParenthesization(StringBuilder sb, int[][] split, int i, int j) {
        if (i == j) {
            sb.append('A').append(i);
            return;
        }
        int k = split[i][j];
        sb.append('(');
        appendParenthesization(sb, split, i, k);
        sb.append(" x ");
        appendParenthesization(sb, split, k + 1, j);
        sb.append(')');
    }
}
