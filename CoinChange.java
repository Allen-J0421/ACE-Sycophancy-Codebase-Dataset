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

    public static int countWays(int sum, int... coins) {
        return count(coins, sum);
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
        if (args.length < 2) {
            printUsage();
            return;
        }

        int sum = parseNonNegativeInt(args[0], "sum");
        int[] coins = new int[args.length - 1];
        for (int i = 1; i < args.length; i++) {
            coins[i - 1] = parsePositiveInt(args[i], "coin");
        }

        System.out.println(countWays(sum, coins));
    }

    private static int parseNonNegativeInt(String value, String label) {
        try {
            int parsed = Integer.parseInt(value);
            if (parsed < 0) {
                throw new IllegalArgumentException(label + " must be non-negative");
            }
            return parsed;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("invalid " + label + ": " + value, ex);
        }
    }

    private static int parsePositiveInt(String value, String label) {
        int parsed = parseNonNegativeInt(value, label);
        if (parsed == 0) {
            throw new IllegalArgumentException(label + " values must be positive");
        }
        return parsed;
    }

    private static void printUsage() {
        System.out.println("Usage: java CoinChange <sum> <coin1> [coin2 ...]");
        System.out.println("Example: java CoinChange 5 1 2 3");
    }
}
