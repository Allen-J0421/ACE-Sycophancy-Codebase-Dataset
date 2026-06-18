import java.util.LinkedHashMap;
import java.util.Map;

class LRUCache {
    private static final int MISSING_VALUE = -1;

    private final Map<Integer, Integer> cache;

    LRUCache(int capacity) {
        this.cache = new BoundedAccessOrderMap(capacity);
    }

    int get(int key) {
        Integer value = cache.get(key);
        return value == null ? MISSING_VALUE : value;
    }

    void put(int key, int value) {
        cache.put(key, value);
    }

    private static final class BoundedAccessOrderMap extends LinkedHashMap<Integer, Integer> {
        private static final long serialVersionUID = 1L;
        private static final float LOAD_FACTOR = 0.75f;
        private static final boolean ACCESS_ORDER = true;

        private final int maxEntries;

        BoundedAccessOrderMap(int maxEntries) {
            super(initialCapacity(maxEntries), LOAD_FACTOR, ACCESS_ORDER);
            this.maxEntries = maxEntries;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
            return size() > maxEntries;
        }

        private static int initialCapacity(int maxEntries) {
            if (maxEntries <= 0) {
                return 1;
            }

            long requiredCapacity = (long) (maxEntries / LOAD_FACTOR) + 1L;
            return requiredCapacity > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) requiredCapacity;
        }
    }
}

class Main {
    public static void main(String[] args) {
        LRUCache cache = new LRUCache(2);

        cache.put(1, 1);
        cache.put(2, 2);
        System.out.println(cache.get(1));
        cache.put(3, 3);
        System.out.println(cache.get(2));
        cache.put(4, 4);
        System.out.println(cache.get(1));
        System.out.println(cache.get(3));
        System.out.println(cache.get(4));
    }
}
