/**
 * Parsed command-line arguments for the coin change utility.
 */
public record CoinChangeArgs(int sum, int[] coins) {

    public CoinChangeArgs {
        coins = coins.clone();
    }

    @Override
    public int[] coins() {
        return coins.clone();
    }

    public static CoinChangeArgs parse(String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("expected at least a sum");
        }

        int sum = parseNonNegativeInt(args[0], "sum");
        int[] coins = new int[args.length - 1];
        for (int i = 1; i < args.length; i++) {
            coins[i - 1] = parsePositiveInt(args[i], "coin");
        }

        return new CoinChangeArgs(sum, CoinChangeDenominations.normalize(coins));
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
}
