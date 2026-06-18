/**
 * Counts the number of ways to make a target sum with unlimited copies of each coin.
 */
public final class CoinChange {

    private CoinChange() {
        // Utility class.
    }

    public static int count(int[] coins, int sum) {
        validateInput(coins, sum);
        return Math.toIntExact(countCombinations(coins, sum));
    }

    private static void validateInput(int[] coins, int sum) {
        if (coins == null) {
            throw new IllegalArgumentException("coins must not be null");
        }
        if (sum < 0) {
            throw new IllegalArgumentException("sum must be non-negative");
        }
        for (int coin : coins) {
            if (coin <= 0) {
                throw new IllegalArgumentException("coin values must be positive");
            }
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

    public static void main(String[] args) {
        int[] coins = {1, 2, 3};
        int sum = 5;
        System.out.println(count(coins, sum));
    }
}
