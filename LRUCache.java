import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V> extends MapBackedCache<K, V> {
    private static final float LOAD_FACTOR = 0.75f;

    public LRUCache(int capacity) {
        this(CacheCapacity.of(capacity));
    }

    private LRUCache(CacheCapacity capacity) {
        super(capacity, createBackingMap(capacity));
    }

    private static <K, V> Map<K, V> createBackingMap(CacheCapacity capacity) {
        return new LinkedHashMap<>(capacity.mapCapacityFor(LOAD_FACTOR), LOAD_FACTOR, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > capacity.value();
            }
        };
    }
}
