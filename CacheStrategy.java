public interface CacheStrategy<K, V> {
    V get(K key);
    void put(K key, V value);
    void remove(K key);
    void clear();
    int getSize();
}

class LRUCache<K, V> implements CacheStrategy<K, V> {
    private final java.util.LinkedHashMap<K, V> cache;
    private final int maxSize;

    public LRUCache(int maxSize) {
        this.maxSize = maxSize;
        this.cache = new java.util.LinkedHashMap<K, V>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
                return size() > maxSize;
            }
        };
    }

    @Override
    public synchronized V get(K key) {
        return cache.get(key);
    }

    @Override
    public synchronized void put(K key, V value) {
        cache.put(key, value);
    }

    @Override
    public synchronized void remove(K key) {
        cache.remove(key);
    }

    @Override
    public synchronized void clear() {
        cache.clear();
    }

    @Override
    public synchronized int getSize() {
        return cache.size();
    }
}

class TTLCache<K, V> implements CacheStrategy<K, V> {
    private final java.util.Map<K, CacheEntry<V>> cache = new java.util.concurrent.ConcurrentHashMap<>();
    private final long ttlMillis;

    public TTLCache(long ttlMillis) {
        this.ttlMillis = ttlMillis;
    }

    @Override
    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry != null && !entry.isExpired()) {
            return entry.value;
        }
        if (entry != null) {
            cache.remove(key);
        }
        return null;
    }

    @Override
    public void put(K key, V value) {
        cache.put(key, new CacheEntry<>(value, ttlMillis));
    }

    @Override
    public void remove(K key) {
        cache.remove(key);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public int getSize() {
        return cache.size();
    }

    private static class CacheEntry<V> {
        V value;
        long expirationTime;

        CacheEntry(V value, long ttlMillis) {
            this.value = value;
            this.expirationTime = System.currentTimeMillis() + ttlMillis;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }
    }
}
