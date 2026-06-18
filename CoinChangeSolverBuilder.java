public class CoinChangeSolverBuilder {
  private CoinChangeStrategy strategy;
  private boolean enableMetrics = false;
  private boolean enableCaching = false;
  private boolean useLRUCache = false;
  private int cacheSize = 100;
  private final java.util.List<SolveEventListener> listeners;

  public CoinChangeSolverBuilder() {
    this.strategy = StrategyFactory.createDefault();
    this.listeners = new java.util.ArrayList<>();
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
    this.enableCaching = true;
    this.useLRUCache = false;
    return this;
  }

  public CoinChangeSolverBuilder withLRUCaching(int maxSize) {
    this.enableCaching = true;
    this.useLRUCache = true;
    this.cacheSize = maxSize;
    return this;
  }

  public CoinChangeSolverBuilder enableMetrics() {
    this.enableMetrics = true;
    return this;
  }

  public CoinChangeSolverBuilder addListener(SolveEventListener listener) {
    if (listener != null) {
      listeners.add(listener);
    }
    return this;
  }

  public CoinChangeSolver build() {
    if (enableCaching) {
      if (useLRUCache) {
        strategy = new LRUCachedStrategy(strategy, cacheSize);
      } else {
        strategy = new CachedStrategy(strategy);
      }
    }

    CoinChangeSolver solver = new CoinChangeSolver(strategy, enableMetrics);
    for (SolveEventListener listener : listeners) {
      solver.addListener(listener);
    }
    return solver;
  }

  public SolverConfiguration buildConfiguration() {
    return new SolverConfiguration.Builder()
        .strategyType(getStrategyType())
        .enableMetrics()
        .enableCaching()
        .cacheSize(cacheSize)
        .useLRUCache()
        .build();
  }

  private StrategyType getStrategyType() {
    return StrategyType.STANDARD;
  }
}
