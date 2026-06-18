package lru;

import static lru.support.TestSupport.expectNullPointer;

public final class LRUCacheNullSafetyTest {
    public static void main(String[] args) {
        shouldRejectNullKeysAndValues();
        System.out.println("LRUCacheNullSafetyTest passed.");
    }

    private static void shouldRejectNullKeysAndValues() {
        Cache<Integer, Integer> cache = new LRUCache<>(1);

        expectNullPointer(() -> cache.get(null));
        expectNullPointer(() -> cache.put(null, 1));
        expectNullPointer(() -> cache.put(1, null));
        expectNullPointer(() -> cache.remove(null));
        expectNullPointer(() -> cache.containsKey(null));
    }
}
