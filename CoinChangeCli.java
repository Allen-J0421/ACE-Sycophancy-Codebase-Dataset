/**
 * Command-line entry point for the coin change utility.
 */
public final class CoinChangeCli {

    private CoinChangeCli() {
        // Utility class.
    }

    public static void main(String[] args) {
        try {
            run(args);
        } catch (IllegalArgumentException ex) {
            System.err.println(ex.getMessage());
            printUsage();
            System.exit(1);
        }
    }

    private static void run(String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("expected at least a sum");
        }

        int sum = parseNonNegativeInt(args[0], "sum");
        int[] coins = new int[args.length - 1];
        for (int i = 1; i < args.length; i++) {
            coins[i - 1] = parsePositiveInt(args[i], "coin");
        }

        System.out.println(CoinChange.countWays(sum, coins));
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
        System.err.println("Usage: java CoinChange <sum> [coin1 coin2 ...]");
        System.err.println("Example: java CoinChange 5 1 2 3");
        System.err.println("Example: java CoinChange 0");
    }
}
