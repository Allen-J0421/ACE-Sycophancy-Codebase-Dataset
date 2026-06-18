public class CoinChangeSolver {
  private final CoinChangeStrategy strategy;
  private final boolean enableMetrics;

  public CoinChangeSolver(CoinChangeStrategy strategy) {
    this(strategy, false);
  }

  public CoinChangeSolver(CoinChangeStrategy strategy, boolean enableMetrics) {
    if (strategy == null) {
      throw new IllegalArgumentException("Strategy cannot be null");
    }
    this.strategy = strategy;
    this.enableMetrics = enableMetrics;
  }

  public CoinChangeResult solve(int[] coins, int targetSum) {
    InputValidator.validate(coins, targetSum);

    if (targetSum == 0) {
      return new CoinChangeResult(1, coins, targetSum);
    }

    long startTime = enableMetrics ? System.nanoTime() : 0;
    Runtime runtime = enableMetrics ? Runtime.getRuntime() : null;
    long memBefore = enableMetrics ? runtime.totalMemory() - runtime.freeMemory() : 0;

    int ways = strategy.countWays(coins, targetSum);

    SolveMetrics metrics = null;
    if (enableMetrics) {
      long endTime = System.nanoTime();
      long memAfter = runtime.totalMemory() - runtime.freeMemory();
      long executionTime = endTime - startTime;
      int memoryUsed = (int) (memAfter - memBefore);
      metrics = new SolveMetrics(executionTime, strategy.getClass().getSimpleName(), memoryUsed);
    }

    return new CoinChangeResult(ways, coins, targetSum, metrics);
  }
}
