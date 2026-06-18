public class CoinChangeSolver {
  private final CoinChangeStrategy strategy;

  public CoinChangeSolver(CoinChangeStrategy strategy) {
    if (strategy == null) {
      throw new IllegalArgumentException("Strategy cannot be null");
    }
    this.strategy = strategy;
  }

  public CoinChangeResult solve(int[] coins, int targetSum) {
    InputValidator.validate(coins, targetSum);

    if (targetSum == 0) {
      return new CoinChangeResult(1, coins, targetSum);
    }

    int ways = strategy.countWays(coins, targetSum);
    return new CoinChangeResult(ways, coins, targetSum);
  }
}
