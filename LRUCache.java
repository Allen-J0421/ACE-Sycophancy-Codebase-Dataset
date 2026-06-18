import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V> extends MapBackedCache<K, V> {
    private static final float LOAD_FACTOR = 0.75f;

    public LRUCache(int capacity) {
        super(capacity, createBackingMap(capacity));
    }

    private static <K, V> Map<K, V> createBackingMap(int capacity) {
        return new LinkedHashMap<>(mapCapacityFor(capacity), LOAD_FACTOR, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > capacity;
            }
        };
    }

    private static int mapCapacityFor(int maxEntries) {
        return Math.max(1, (int) Math.ceil(maxEntries / LOAD_FACTOR));
    }
}
