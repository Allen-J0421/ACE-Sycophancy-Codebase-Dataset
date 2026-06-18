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
        validateSum(sum);
        int[] normalizedCoins = normalizeCoins(coins);
        return Math.toIntExact(countCombinations(normalizedCoins, sum));
    }

    /**
     * Convenience overload for callers that already have coins as varargs.
     */
    public static int countWays(int sum, int... coins) {
        return count(coins, sum);
    }

    public static void main(String[] args) {
        CoinChangeCli.main(args);
    }

    private static void validateSum(int sum) {
        if (sum < 0) {
            throw new IllegalArgumentException("sum must be non-negative");
        }
    }

    private static int[] normalizeCoins(int[] coins) {
        if (coins == null) {
            throw new IllegalArgumentException("coins must not be null");
        }
        if (coins.length == 0) {
            return coins;
        }

        int[] normalized = coins.clone();
        java.util.Arrays.sort(normalized);

        int uniqueCount = 0;
        int previous = 0;
        for (int coin : normalized) {
            if (coin <= 0) {
                throw new IllegalArgumentException("coin values must be positive");
            }
            if (uniqueCount == 0 || coin != previous) {
                normalized[uniqueCount++] = coin;
                previous = coin;
            }
        }

        int[] uniqueCoins = new int[uniqueCount];
        System.arraycopy(normalized, 0, uniqueCoins, 0, uniqueCount);
        return uniqueCoins;
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
