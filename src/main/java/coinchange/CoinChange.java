package coinchange;

import java.util.Objects;

public final class CoinChange {
    private static final CoinChange DEFAULT_INSTANCE = new CoinChange(new DynamicProgrammingCoinChangeSolver());
    private final CoinChangeSolver solver;

    public CoinChange(CoinChangeSolver solver) {
        this.solver = Objects.requireNonNull(solver, "solver must not be null");
    }

    public static int count(int[] coins, int targetSum) {
        return standard().countWays(coins, targetSum);
    }

    public static int count(CoinDenominations denominations, int targetSum) {
        return standard().countWays(denominations, targetSum);
    }

    public static int count(CoinChangeRequest request) {
        return standard().countWays(request);
    }

    public static CoinChange standard() {
        return DEFAULT_INSTANCE;
    }

    public int countWays(int[] coins, int targetSum) {
        return countWays(new CoinChangeRequest(coins, targetSum));
    }

    public int countWays(CoinDenominations denominations, int targetSum) {
        return countWays(new CoinChangeRequest(denominations, targetSum));
    }

    public int countWays(CoinChangeRequest request) {
        return solver.countWays(request);
    }

    public CoinChangeSolver solver() {
        return solver;
    }
}
