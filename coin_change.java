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
     * @throws IllegalArgumentException if {@code coins} is null/empty, any coin is non-positive,
     *                                  or {@code sum} is negative
     */
    public static int count(int[] coins, int sum) {
        if (coins == null || coins.length == 0) {
            throw new IllegalArgumentException("coins must be non-null and non-empty");
        }
        for (int coin : coins) {
            if (coin <= 0) {
                throw new IllegalArgumentException("each coin must be a positive integer, got: " + coin);
            }
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

    // Usage: java CoinChange <sum> <coin1> [coin2 ...]
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println(count(new int[]{1, 2, 3}, 5));
            return;
        }
        if (args.length == 1) {
            System.err.println("Usage: java CoinChange <sum> <coin1> [coin2 ...]");
            System.exit(1);
        }
        try {
            int sum = Integer.parseInt(args[0]);
            int[] coins = new int[args.length - 1];
            for (int i = 1; i < args.length; i++) {
                coins[i - 1] = Integer.parseInt(args[i]);
            }
            System.out.println(count(coins, sum));
        } catch (NumberFormatException e) {
            System.err.println("Error: all arguments must be integers. " + e.getMessage());
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}
