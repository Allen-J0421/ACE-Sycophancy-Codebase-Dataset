public class SolverConfiguration {
  private final StrategyType strategyType;
  private final boolean enableMetrics;
  private final boolean enableCaching;
  private final int cacheSize;
  private final boolean enableEvents;
  private final boolean useLRUCache;

  public SolverConfiguration(StrategyType strategyType, boolean enableMetrics,
      boolean enableCaching, int cacheSize, boolean enableEvents, boolean useLRUCache) {
    this.strategyType = strategyType;
    this.enableMetrics = enableMetrics;
    this.enableCaching = enableCaching;
    this.cacheSize = cacheSize;
    this.enableEvents = enableEvents;
    this.useLRUCache = useLRUCache;
  }

  public StrategyType getStrategyType() { return strategyType; }
  public boolean isMetricsEnabled() { return enableMetrics; }
  public boolean isCachingEnabled() { return enableCaching; }
  public int getCacheSize() { return cacheSize; }
  public boolean isEventsEnabled() { return enableEvents; }
  public boolean useLRUCache() { return useLRUCache; }

  public static class Builder {
    private StrategyType strategyType = StrategyType.STANDARD;
    private boolean enableMetrics = false;
    private boolean enableCaching = false;
    private int cacheSize = 100;
    private boolean enableEvents = false;
    private boolean useLRUCache = false;

    public Builder strategyType(StrategyType type) {
      this.strategyType = type;
      return this;
    }

    public Builder enableMetrics() {
      this.enableMetrics = true;
      return this;
    }

    public Builder enableCaching() {
      this.enableCaching = true;
      return this;
    }

    public Builder cacheSize(int size) {
      this.cacheSize = size;
      return this;
    }

    public Builder enableEvents() {
      this.enableEvents = true;
      return this;
    }

    public Builder useLRUCache() {
      this.useLRUCache = true;
      return this;
    }

    public SolverConfiguration build() {
      return new SolverConfiguration(strategyType, enableMetrics, enableCaching,
          cacheSize, enableEvents, useLRUCache);
    }
  }
}
