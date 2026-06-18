import java.util.LinkedHashMap;
import java.util.Map;

class LRUCache {
    private static final int MISSING_VALUE = -1;

    private final Map<Integer, Integer> cache;

    LRUCache(int capacity) {
        this.cache = new BoundedAccessOrderMap<>(requireNonNegativeCapacity(capacity));
    }

    int get(int key) {
        Integer value = cache.get(key);
        return value == null ? MISSING_VALUE : value;
    }

    void put(int key, int value) {
        cache.put(key, value);
    }

    private static int requireNonNegativeCapacity(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity must be non-negative");
        }

        return capacity;
    }

    private static final class BoundedAccessOrderMap<K, V> extends LinkedHashMap<K, V> {
        private static final long serialVersionUID = 1L;
        private static final float LOAD_FACTOR = 0.75f;
        private static final boolean ACCESS_ORDER = true;
        private static final int MIN_INITIAL_CAPACITY = 1;
        private static final int MAX_INITIAL_CAPACITY = 1 << 30;

        private final int maxEntries;

        BoundedAccessOrderMap(int maxEntries) {
            super(initialCapacity(maxEntries), LOAD_FACTOR, ACCESS_ORDER);
            this.maxEntries = maxEntries;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > maxEntries;
        }

        private static int initialCapacity(int maxEntries) {
            if (maxEntries <= 0) {
                return MIN_INITIAL_CAPACITY;
            }

            long requiredCapacity = (long) Math.ceil(maxEntries / (double) LOAD_FACTOR) + 1L;
            return requiredCapacity > MAX_INITIAL_CAPACITY
                    ? MAX_INITIAL_CAPACITY
                    : (int) requiredCapacity;
        }
    }
}

class Main {
    public static void main(String[] args) {
        LRUCache cache = new LRUCache(2);

        cache.put(1, 1);
        cache.put(2, 2);
        printLookup(cache, 1);
        cache.put(3, 3);
        printLookup(cache, 2);
        cache.put(4, 4);
        printLookup(cache, 1);
        printLookup(cache, 3);
        printLookup(cache, 4);
    }

    private static void printLookup(LRUCache cache, int key) {
        System.out.println(cache.get(key));
    }
}
