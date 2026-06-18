/**
 * Utility for counting the number of ways to make a target sum with unlimited copies of each coin.
 */
public final class CoinChange {

    private CoinChange() {
        // Utility class.
    }

    /**
     * Counts the number of unique combinations that sum to {@code sum}.
     * Duplicate coin denominations are ignored.
     */
    public static int count(int[] coins, int sum) {
        return Math.toIntExact(countLong(coins, sum));
    }

    /**
     * Returns the exact number of unique combinations that sum to {@code sum}.
     * Duplicate coin denominations are ignored.
     */
    public static long countLong(int[] coins, int sum) {
        validateSum(sum);
        int[] normalizedCoins = CoinChangeDenominations.normalize(coins);
        return countCombinations(normalizedCoins, sum);
    }

    /**
     * Convenience overload for callers that already have coins as varargs.
     */
    public static int countWays(int sum, int... coins) {
        return count(coins, sum);
    }

    /**
     * Convenience overload that returns the exact combination count as a long.
     */
    public static long countWaysLong(int sum, int... coins) {
        return countLong(coins, sum);
    }

    public static void main(String[] args) {
        CoinChangeCli.main(args);
    }

    private static void validateSum(int sum) {
        if (sum < 0) {
            throw new IllegalArgumentException("sum must be non-negative");
        }
    }

    private static long countCombinations(int[] coins, int sum) {
        long[] ways = new long[sum + 1];
        ways[0] = 1L;

        for (int coin : coins) {
            for (int current = coin; current <= sum; current++) {
                ways[current] += ways[current - coin];
            }
        }

        return ways[sum];
    }
}
