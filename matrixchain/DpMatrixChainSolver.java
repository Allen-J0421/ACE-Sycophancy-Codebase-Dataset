package matrixchain;

public class DpMatrixChainSolver implements MatrixChainSolver {

    @Override
    public Result solve(int[] dims) {
        int n = dims.length;
        int[][] cost = new int[n][n];
        int[][] split = new int[n][n];

        for (int len = 2; len < n; len++) {
            for (int i = 0; i < n - len; i++) {
                int j = i + len;
                cost[i][j] = Integer.MAX_VALUE;

                for (int k = i + 1; k < j; k++) {
                    int c = cost[i][k] + cost[k][j] + dims[i] * dims[k] * dims[j];
                    if (c < cost[i][j]) {
                        cost[i][j] = c;
                        split[i][j] = k;
                    }
                }
            }
        }

        return new Result(cost[0][n - 1], split);
    }
}
