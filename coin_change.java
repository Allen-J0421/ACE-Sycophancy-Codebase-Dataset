public class CoinChange {

    /**
     * Counts the number of ways to make {@code sum} using an unlimited supply of
     * the given coin denominations (combinations, not permutations).
     *
     * <p>Time: O(coins.length * sum) — Space: O(sum)
     *
     * @param coins positive coin denominations (non-null, non-empty)
     * @param sum   target sum (non-negative)
     * @return number of distinct combinations that total to {@code sum}
     * @throws IllegalArgumentException if {@code coins} is null/empty or {@code sum} is negative
     */
    static int count(int[] coins, int sum) {
        if (coins == null || coins.length == 0) {
            throw new IllegalArgumentException("coins must be non-null and non-empty");
        }
        if (sum < 0) {
            throw new IllegalArgumentException("sum must be non-negative");
        }

        int[] dp = new int[sum + 1];
        dp[0] = 1;

        for (int coin : coins) {
            for (int j = coin; j <= sum; j++) {
                dp[j] += dp[j - coin];
            }
        }

        return dp[sum];
    }

    public static void main(String[] args) {
        int[] coins = {1, 2, 3};
        int sum = 5;
        System.out.println(count(coins, sum));
    }
}
