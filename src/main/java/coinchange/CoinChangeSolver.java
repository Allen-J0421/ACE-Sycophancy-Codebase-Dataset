package coinchange;

public interface CoinChangeSolver {

    CoinChangeSolution solve(CoinChangeProblem problem);

    default int countWays(int[] coins, int targetSum) {
        return countWays(new CoinChangeProblem(coins, targetSum));
    }

    default int countWays(CoinDenominations denominations, int targetSum) {
        return countWays(new CoinChangeProblem(denominations, targetSum));
    }

    default int countWays(CoinChangeProblem problem) {
        return solve(problem).ways();
    }

    default CoinChangeSolution solve(int[] coins, int targetSum) {
        return solve(new CoinChangeProblem(coins, targetSum));
    }

    default CoinChangeSolution solve(CoinDenominations denominations, int targetSum) {
        return solve(new CoinChangeProblem(denominations, targetSum));
    }
}
