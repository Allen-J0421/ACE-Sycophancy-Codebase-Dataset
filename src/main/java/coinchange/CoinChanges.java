package coinchange;

public final class CoinChanges {
    private static final CoinChange STANDARD = using(new DynamicProgrammingCoinChangeSolver());

    private CoinChanges() {
    }

    public static CoinChange standard() {
        return STANDARD;
    }

    public static CoinChange using(CoinChangeSolver solver) {
        return new CoinChange(solver);
    }

    public static int count(int[] coins, int targetSum) {
        return solve(coins, targetSum).ways();
    }

    public static int count(CoinDenominations denominations, int targetSum) {
        return solve(denominations, targetSum).ways();
    }

    public static int count(CoinChangeProblem problem) {
        return solve(problem).ways();
    }

    public static CoinChangeSolution solve(int[] coins, int targetSum) {
        return standard().solve(new CoinChangeProblem(coins, targetSum));
    }

    public static CoinChangeSolution solve(CoinDenominations denominations, int targetSum) {
        return standard().solve(new CoinChangeProblem(denominations, targetSum));
    }

    public static CoinChangeSolution solve(CoinChangeProblem problem) {
        return standard().solve(problem);
    }
}
