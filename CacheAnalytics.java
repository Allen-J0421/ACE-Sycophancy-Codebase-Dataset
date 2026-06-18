public class CacheAnalytics {
  private final CacheStatistics stats;

  public CacheAnalytics() {
    this.stats = new CacheStatistics();
  }

  public void recordCacheHit() {
    stats.recordHit();
  }

  public void recordCacheMiss() {
    stats.recordMiss();
  }

  public void recordEviction() {
    stats.recordEviction();
  }

  public CacheStatistics getStatistics() {
    return stats;
  }

  public double getEfficiencyScore() {
    double hitRate = stats.getHitRate();
    long totalOps = stats.getTotalRequests();
    return totalOps > 0 ? hitRate * 100 : 0;
  }

  public String generateReport() {
    return String.format(
        "=== Cache Analytics Report ===\n" +
        "  Total Requests: %d\n" +
        "  Cache Hits: %d\n" +
        "  Cache Misses: %d\n" +
        "  Evictions: %d\n" +
        "  Hit Rate: %.2f%%\n" +
        "  Efficiency Score: %.2f%%",
        stats.getTotalRequests(), stats.getHits(), stats.getMisses(),
        stats.getEvictions(), stats.getHitRate() * 100,
        getEfficiencyScore()
    );
  }

  public void reset() {
    stats.reset();
  }
}
