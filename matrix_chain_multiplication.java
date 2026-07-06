class MatrixChainResult {
    final int minCost;
    final int[][] splitTable;

    MatrixChainResult(int minCost, int[][] splitTable) {
        this.minCost = minCost;
        this.splitTable = splitTable;
    }
}

class MatrixChainSolver {

    MatrixChainResult solve(int[] dims) {
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

        return new MatrixChainResult(cost[0][n - 1], split);
    }

    String optimalOrder(int[][] split, int i, int j) {
        if (i + 1 == j) {
            return "M" + j;
        }
        int k = split[i][j];
        return "(" + optimalOrder(split, i, k) + " x " + optimalOrder(split, k, j) + ")";
    }
}

class MatrixChainMultiplication {

    public static void main(String[] args) {
        int[] dims = { 2, 1, 3, 4 };

        MatrixChainSolver solver = new MatrixChainSolver();
        MatrixChainResult result = solver.solve(dims);

        System.out.println(result.minCost);
    }
}
