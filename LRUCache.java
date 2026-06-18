import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A least-recently-used cache for integer keys and values.
 */
public final class LRUCache {
    private final int capacity;
    private final LinkedHashMap<Integer, Integer> entries;

    public LRUCache(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity must be non-negative");
        }

        this.capacity = capacity;
        this.entries = new LinkedHashMap<Integer, Integer>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
                return size() > LRUCache.this.capacity;
            }
        };
    }

    public int get(int key) {
        Integer value = entries.get(key);
        return value != null ? value : -1;
    }

    public void put(int key, int value) {
        entries.put(key, value);
    }

    public int size() {
        return entries.size();
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }
}
