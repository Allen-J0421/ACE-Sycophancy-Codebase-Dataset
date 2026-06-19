package coinchange;

public interface CoinChangeSolver {

    int countWays(CoinChangeRequest request);

    default int countWays(int[] coins, int targetSum) {
        return countWays(new CoinChangeRequest(coins, targetSum));
    }

    default int countWays(CoinDenominations denominations, int targetSum) {
        return countWays(new CoinChangeRequest(denominations, targetSum));
    }
}
