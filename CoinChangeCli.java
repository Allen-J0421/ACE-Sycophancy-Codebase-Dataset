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
        CoinChangeArgs parsedArgs = CoinChangeArgs.parse(args);
        System.out.println(CoinChange.countWaysLong(parsedArgs.sum(), parsedArgs.coins()));
    }

    private static void printUsage() {
        System.err.println("Usage: java CoinChange <sum> [coin1 coin2 ...]");
        System.err.println("Example: java CoinChange 5 1 2 3");
        System.err.println("Example: java CoinChange 0");
    }
}
