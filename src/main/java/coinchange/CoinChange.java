package coinchange;

import java.util.Objects;

public final class CoinChange implements CoinChangeSolver {
    private static final CoinChange DEFAULT_INSTANCE = using(new DynamicProgrammingCoinChangeSolver());
    private final CoinChangeSolver solver;

    private CoinChange(CoinChangeSolver solver) {
        this.solver = solver;
    }

    public static int count(int[] coins, int targetSum) {
        return solveProblem(coins, targetSum).ways();
    }

    public static int count(CoinDenominations denominations, int targetSum) {
        return solveProblem(denominations, targetSum).ways();
    }

    public static int count(CoinChangeProblem problem) {
        return solveProblem(problem).ways();
    }

    public static CoinChangeSolution solveProblem(int[] coins, int targetSum) {
        return standard().solve(new CoinChangeProblem(coins, targetSum));
    }

    public static CoinChangeSolution solveProblem(CoinDenominations denominations, int targetSum) {
        return standard().solve(new CoinChangeProblem(denominations, targetSum));
    }

    public static CoinChangeSolution solveProblem(CoinChangeProblem problem) {
        return standard().solve(problem);
    }

    public static CoinChange standard() {
        return DEFAULT_INSTANCE;
    }

    public static CoinChange using(CoinChangeSolver solver) {
        return new CoinChange(Objects.requireNonNull(solver, "solver must not be null"));
    }

    @Override
    public CoinChangeSolution solve(CoinChangeProblem problem) {
        return solver.solve(problem);
    }

    public CoinChangeSolver solver() {
        return solver;
    }
}
