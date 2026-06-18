public class CoinChangeResult {
  private final int ways;
  private final int[] coins;
  private final int targetSum;
  private final SolveMetrics metrics;

  public CoinChangeResult(int ways, int[] coins, int targetSum) {
    this(ways, coins, targetSum, null);
  }

  public CoinChangeResult(int ways, int[] coins, int targetSum, SolveMetrics metrics) {
    this.ways = ways;
    this.coins = coins.clone();
    this.targetSum = targetSum;
    this.metrics = metrics;
  }

  public int getWays() {
    return ways;
  }

  public int[] getCoins() {
    return coins.clone();
  }

  public int getTargetSum() {
    return targetSum;
  }

  public SolveMetrics getMetrics() {
    return metrics;
  }

  public boolean hasMetrics() {
    return metrics != null;
  }

  @Override
  public String toString() {
    String base = String.format("CoinChangeResult{ways=%d, targetSum=%d, numCoins=%d}",
        ways, targetSum, coins.length);
    if (metrics != null) {
      base += " " + metrics;
    }
    return base;
  }
}
