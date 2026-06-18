public class Adapter implements CoinChangeStrategy {
  private final Object legacyComponent;

  public Adapter(Object legacyComponent) {
    this.legacyComponent = legacyComponent;
  }

  @Override
  public int countWays(int[] coins, int targetSum) {
    if (legacyComponent instanceof CoinChangeStrategy) {
      return ((CoinChangeStrategy) legacyComponent).countWays(coins, targetSum);
    }
    throw new UnsupportedOperationException(
        "Cannot adapt: " + legacyComponent.getClass().getName());
  }
}
