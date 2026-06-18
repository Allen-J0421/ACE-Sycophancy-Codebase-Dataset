public final class LRUCacheFactory implements CacheFactory {
    @Override
    public <K, V> Cache<K, V> create(int capacity) {
        return new LRUCache<>(capacity);
    }
}
