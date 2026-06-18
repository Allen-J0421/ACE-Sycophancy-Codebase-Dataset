public class Proxy implements CoinChangeStrategy {
  private final CoinChangeStrategy realSubject;
  private int accessCount = 0;
  private final int accessLimit;

  public Proxy(CoinChangeStrategy realSubject, int accessLimit) {
    this.realSubject = realSubject;
    this.accessLimit = accessLimit;
  }

  @Override
  public int countWays(int[] coins, int targetSum) {
    accessCount++;

    if (accessCount > accessLimit) {
      throw new RuntimeException("Access limit exceeded: " + accessLimit);
    }

    return realSubject.countWays(coins, targetSum);
  }

  public int getAccessCount() {
    return accessCount;
  }

  public int getAccessLimit() {
    return accessLimit;
  }

  public double getUsagePercentage() {
    return (double) accessCount / accessLimit * 100;
  }
}
