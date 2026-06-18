import java.util.LinkedHashMap;
import java.util.Map;

class LRUCache {
    private static final int MISSING_VALUE = -1;

    private final int capacity;
    private final Map<Integer, Integer> cache;

    LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new BoundedAccessOrderMap();
    }

    int get(int key) {
        return cache.getOrDefault(key, MISSING_VALUE);
    }

    void put(int key, int value) {
        cache.put(key, value);
    }

    private class BoundedAccessOrderMap extends LinkedHashMap<Integer, Integer> {
        BoundedAccessOrderMap() {
            super(16, 0.75f, true);
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
            return size() > capacity;
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
