public class CacheMiddleware implements SolveMiddleware {
  private final CacheAnalytics analytics;
  private final java.util.Map<String, Integer> requestCache;

  public CacheMiddleware() {
    this.analytics = new CacheAnalytics();
    this.requestCache = new java.util.HashMap<>();
  }

  @Override
  public CoinChangeResult process(SolveContext context, SolveChain chain) {
    String cacheKey = generateKey(context.getCoins(), context.getTargetSum());

    if (requestCache.containsKey(cacheKey)) {
      analytics.recordCacheHit();
      int cachedWays = requestCache.get(cacheKey);
      return new CoinChangeResult(cachedWays, context.getCoins(), context.getTargetSum());
    }

    analytics.recordCacheMiss();
    CoinChangeResult result = chain.execute(context);
    requestCache.put(cacheKey, result.getWays());

    return result;
  }

  public CacheStatistics getStatistics() {
    return analytics.getStatistics();
  }

  public String getAnalyticsReport() {
    return analytics.generateReport();
  }

  public void clearCache() {
    requestCache.clear();
    analytics.reset();
  }

  private String generateKey(int[] coins, int sum) {
    StringBuilder sb = new StringBuilder();
    for (int coin : coins) {
      sb.append(coin).append(",");
    }
    return sb.append(sum).toString();
  }
}
