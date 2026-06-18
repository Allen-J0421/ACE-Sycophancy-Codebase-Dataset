public enum StrategyType {
  STANDARD("Standard 2D DP", DynamicProgrammingStrategy.class),
  SPACE_OPTIMIZED("Space Optimized 1D DP", SpaceOptimizedStrategy.class);

  private final String description;
  private final Class<? extends CoinChangeStrategy> strategyClass;

  StrategyType(String description, Class<? extends CoinChangeStrategy> strategyClass) {
    this.description = description;
    this.strategyClass = strategyClass;
  }

  public String getDescription() {
    return description;
  }

  public Class<? extends CoinChangeStrategy> getStrategyClass() {
    return strategyClass;
  }
}
