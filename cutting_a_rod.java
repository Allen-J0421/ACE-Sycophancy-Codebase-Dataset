class CuttingRod {

    static int cutRod(int[] price) {
        int n = price.length-1;
        int[] dp = new int[n + 1];

        for (int i = 1; i <= n; i++) {

            for (int j = 1; j <= i; j++) {
                dp[i] = Math.max(dp[i], price[j] + dp[i - j]);
            }
        }

        return dp[n];
    }

    public static void main(String[] args) {
        int[] price = {0, 1, 5, 8, 9, 10, 17, 17, 20};
        System.out.println(cutRod(price));
    }
}
