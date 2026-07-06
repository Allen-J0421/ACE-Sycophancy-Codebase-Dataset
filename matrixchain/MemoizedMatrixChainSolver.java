package matrixchain;

public class MemoizedMatrixChainSolver implements MatrixChainSolver {

    @Override
    public Result solve(int[] dims) {
        int n = dims.length;
        Integer[][] memo = new Integer[n][n];
        int[][] split = new int[n][n];

        computeCost(dims, memo, split, 0, n - 1);

        return new Result(memo[0][n - 1], split);
    }

    private int computeCost(int[] dims, Integer[][] memo, int[][] split, int i, int j) {
        if (i + 1 >= j) return 0;
        if (memo[i][j] != null) return memo[i][j];

        int minCost = Integer.MAX_VALUE;
        for (int k = i + 1; k < j; k++) {
            int c = computeCost(dims, memo, split, i, k)
                  + computeCost(dims, memo, split, k, j)
                  + dims[i] * dims[k] * dims[j];
            if (c < minCost) {
                minCost = c;
                split[i][j] = k;
            }
        }

        memo[i][j] = minCost;
        return minCost;
    }
}
