package coinchange;

public final class CoinChange {
    private static final CoinChangeSolver DEFAULT_SOLVER = new DynamicProgrammingCoinChangeSolver();

    private CoinChange() {
    }

    public static int count(int[] coins, int targetSum) {
        return count(new CoinChangeRequest(coins, targetSum));
    }

    public static int count(CoinDenominations denominations, int targetSum) {
        return count(new CoinChangeRequest(denominations, targetSum));
    }

    public static int count(CoinChangeRequest request) {
        return DEFAULT_SOLVER.countWays(request);
    }
}
