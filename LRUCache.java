import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V> {
    private final Map<K, V> map;

    public LRUCache(int capacity) {
        this.map = new LinkedHashMap<K, V>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > capacity;
            }
        };
    }

    public V get(K key) {
        return map.get(key);
    }

    public void put(K key, V value) {
        map.put(key, value);
    }
}
