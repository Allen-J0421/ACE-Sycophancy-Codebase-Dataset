package lru;

import static lru.support.TestSupport.assertEquals;
import static lru.support.TestSupport.assertNull;
import static lru.support.TestSupport.assertTrue;

public final class LRUCacheBehaviorTest {
    public static void main(String[] args) {
        shouldEvictLeastRecentlyUsedEntry();
        shouldUpdateExistingEntryWithoutGrowing();
        System.out.println("LRUCacheBehaviorTest passed.");
    }

    private static void shouldEvictLeastRecentlyUsedEntry() {
        Cache<Integer, Integer> cache = new LRUCache<>(2);

        cache.put(1, 1);
        cache.put(2, 2);
        assertEquals(1, cache.get(1), "recent access should return the value");

        cache.put(3, 3);
        assertNull(cache.get(2), "least recently used entry should be evicted");
        assertEquals(2, cache.size(), "cache size should reflect surviving entries");
        assertTrue(cache.containsKey(1), "key 1 should remain present");
        assertTrue(cache.containsKey(3), "key 3 should be present");
    }

    private static void shouldUpdateExistingEntryWithoutGrowing() {
        Cache<Integer, Integer> cache = new LRUCache<>(2);

        cache.put(1, 1);
        cache.put(1, 10);

        assertEquals(10, cache.get(1), "updated value should be returned");
        assertEquals(1, cache.size(), "updating an entry should not create duplicates");
    }
}
