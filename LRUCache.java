import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache {
    private static final int NOT_FOUND = -1;
    private static final float LOAD_FACTOR = 0.75f;

    private final int capacity;
    private final Map<Integer, Integer> cache;

    public LRUCache(int capacity) {
        this.capacity = validateCapacity(capacity);
        this.cache = new LinkedHashMap<>(mapCapacityFor(capacity), LOAD_FACTOR, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
                return size() > LRUCache.this.capacity;
            }
        };
    }

    public int get(int key) {
        return cache.getOrDefault(key, NOT_FOUND);
    }

    public void put(int key, int value) {
        cache.put(key, value);
    }

    public int size() {
        return cache.size();
    }

    private static int validateCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be greater than 0");
        }

        return capacity;
    }

    private static int mapCapacityFor(int maxEntries) {
        return Math.max(1, (int) Math.ceil(maxEntries / LOAD_FACTOR));
    }
}
