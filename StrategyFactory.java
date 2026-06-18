public class StrategyFactory {

  public static CoinChangeStrategy create(StrategyType type) {
    return create(type, false);
  }

  public static CoinChangeStrategy create(StrategyType type, boolean enableCache) {
    CoinChangeStrategy strategy;

    try {
      strategy = type.getStrategyClass().getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      throw new RuntimeException("Failed to instantiate strategy: " + type, e);
    }

    if (enableCache) {
      strategy = new CachedStrategy(strategy);
    }

    return strategy;
  }

  public static CoinChangeStrategy createDefault() {
    return create(StrategyType.STANDARD);
  }

  public static CoinChangeStrategy createCached() {
    return create(StrategyType.STANDARD, true);
  }

  public static CoinChangeStrategy createOptimal(int[] coins, int targetSum) {
    // Adaptive selection based on problem characteristics
    if (targetSum > 1000) {
      // Space-optimized for large sums
      return create(StrategyType.SPACE_OPTIMIZED, true);
    } else if (coins.length > 100) {
      // Cached for many coins
      return create(StrategyType.STANDARD, true);
    } else {
      // Standard for typical cases
      return create(StrategyType.STANDARD);
    }
  }
}
