public class CoinChangeSolverBuilder {
  private CoinChangeStrategy strategy;
  private boolean enableMetrics = false;

  public CoinChangeSolverBuilder() {
    this.strategy = StrategyFactory.createDefault();
  }

  public CoinChangeSolverBuilder withStrategy(CoinChangeStrategy strategy) {
    if (strategy == null) {
      throw new IllegalArgumentException("Strategy cannot be null");
    }
    this.strategy = strategy;
    return this;
  }

  public CoinChangeSolverBuilder withStrategyType(StrategyType type) {
    this.strategy = StrategyFactory.create(type);
    return this;
  }

  public CoinChangeSolverBuilder withCaching() {
    this.strategy = new CachedStrategy(strategy);
    return this;
  }

  public CoinChangeSolverBuilder enableMetrics() {
    this.enableMetrics = true;
    return this;
  }

  public CoinChangeSolver build() {
    return new CoinChangeSolver(strategy, enableMetrics);
  }
}
