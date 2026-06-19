package lru;

/**
 * An {@link EvictingCache} preconfigured with a {@link LruEvictionPolicy}.
 *
 * <p>LRU is no longer baked into the cache — it is simply the eviction strategy
 * this convenience subclass installs. For other behaviours, construct an
 * {@link EvictingCache} directly with the desired {@link EvictionPolicy}.
 */
public class LRUCache<K, V> extends EvictingCache<K, V> {

    public LRUCache(int capacity) {
        super(capacity, new LruEvictionPolicy<>());
    }
}
