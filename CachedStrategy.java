import java.util.HashMap;
import java.util.Map;

public class CachedStrategy implements CoinChangeStrategy {
  private final CoinChangeStrategy delegate;
  private final Map<CacheKey, Integer> cache;

  public CachedStrategy(CoinChangeStrategy delegate) {
    this.delegate = delegate;
    this.cache = new HashMap<>();
  }

  @Override
  public int countWays(int[] coins, int targetSum) {
    CacheKey key = new CacheKey(coins, targetSum);
    return cache.computeIfAbsent(key, k -> delegate.countWays(coins, targetSum));
  }

  public void clearCache() {
    cache.clear();
  }

  public int getCacheSize() {
    return cache.size();
  }
}
