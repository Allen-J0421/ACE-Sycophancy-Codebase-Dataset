public class CoinChangeResult {
  private final int ways;
  private final int[] coins;
  private final int targetSum;

  public CoinChangeResult(int ways, int[] coins, int targetSum) {
    this.ways = ways;
    this.coins = coins.clone();
    this.targetSum = targetSum;
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

  @Override
  public String toString() {
    return String.format("CoinChangeResult{ways=%d, targetSum=%d, numCoins=%d}",
        ways, targetSum, coins.length);
  }
}
