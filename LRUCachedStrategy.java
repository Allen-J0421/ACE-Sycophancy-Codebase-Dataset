import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCachedStrategy implements CoinChangeStrategy {
  private final CoinChangeStrategy delegate;
  private final int maxSize;
  private final Map<CacheKey, Integer> cache;

  public LRUCachedStrategy(CoinChangeStrategy delegate, int maxSize) {
    this.delegate = delegate;
    this.maxSize = maxSize;
    this.cache = new LinkedHashMap<CacheKey, Integer>(maxSize, 0.75f, true) {
      protected boolean removeEldestEntry(Map.Entry<CacheKey, Integer> eldest) {
        return size() > maxSize;
      }
    };
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

  public int getMaxCacheSize() {
    return maxSize;
  }
}
