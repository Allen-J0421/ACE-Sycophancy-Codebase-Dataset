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
}
