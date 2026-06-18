import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V> extends AbstractCache<K, V> {
    private static final float LOAD_FACTOR = 0.75f;

    private final Map<K, V> cache;

    public LRUCache(int capacity) {
        super(capacity);
        this.cache = new LinkedHashMap<>(mapCapacityFor(capacity), LOAD_FACTOR, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > LRUCache.this.capacity();
            }
        };
    }

    @Override
    protected V getValue(K key) {
        return cache.get(key);
    }

    @Override
    protected boolean containsKeyValue(K key) {
        return cache.containsKey(key);
    }

    @Override
    protected void putValue(K key, V value) {
        cache.put(key, value);
    }

    @Override
    protected V removeValue(K key) {
        return cache.remove(key);
    }

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public void clear() {
        cache.clear();
    }

    private static int mapCapacityFor(int maxEntries) {
        return Math.max(1, (int) Math.ceil(maxEntries / LOAD_FACTOR));
    }
}
