package coinchange;

import java.util.Objects;

public final class CoinChange implements CoinChangeSolver {
    private static final CoinChange DEFAULT_INSTANCE = using(new DynamicProgrammingCoinChangeSolver());
    private final CoinChangeSolver solver;

    private CoinChange(CoinChangeSolver solver) {
        this.solver = solver;
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

    public static CoinChange using(CoinChangeSolver solver) {
        return new CoinChange(Objects.requireNonNull(solver, "solver must not be null"));
    }

    @Override
    public int countWays(CoinChangeRequest request) {
        return solver.countWays(request);
    }

    public CoinChangeSolver solver() {
        return solver;
    }
}
