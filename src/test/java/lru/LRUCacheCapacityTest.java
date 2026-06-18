package lru;

import static lru.support.TestSupport.assertNull;
import static lru.support.TestSupport.assertTrue;

public final class LRUCacheCapacityTest {
    public static void main(String[] args) {
        shouldHandleZeroCapacity();
        shouldRejectNegativeCapacity();
        System.out.println("LRUCacheCapacityTest passed.");
    }

    private static void shouldHandleZeroCapacity() {
        Cache<Integer, Integer> cache = new LRUCache<>(0);

        cache.put(1, 1);

        assertNull(cache.get(1), "zero-capacity cache should not store entries");
        assertTrue(cache.isEmpty(), "zero-capacity cache should stay empty");
    }

    private static void shouldRejectNegativeCapacity() {
        try {
            new LRUCache<>(-1);
            throw new AssertionError("negative capacity should fail");
        } catch (IllegalArgumentException expected) {
            // Expected.
        }
    }
}
