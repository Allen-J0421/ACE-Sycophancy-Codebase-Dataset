class MatrixChainMultiplication {

    static int matrixMultiplication(int[] arr)
    {

        int n = arr.length;

        int[][] dp = new int[n][n];

        for (int len = 2; len < n; len++) {
            for (int i = 0; i < n - len; i++) {
                int j = i + len;
                dp[i][j] = Integer.MAX_VALUE;

                for (int k = i + 1; k < j; k++) {
                    int cost = dp[i][k] + dp[k][j]
                               + arr[i] * arr[k] * arr[j];
                    if (cost < dp[i][j]) {
                        dp[i][j] = cost;
                    }
                }
            }
        }

        return dp[0][n - 1];
    }

    public static void main(String[] args)
    {

        int[] arr = { 2, 1, 3, 4 };
        System.out.println(matrixMultiplication(arr));
    }
}
