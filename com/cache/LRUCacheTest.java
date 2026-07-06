package com.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LRUCacheTest {

    private Cache<Integer, Integer> cache;

    @BeforeEach
    void setUp() {
        cache = new LRUCache<>(2);
    }

    @Test
    void get_returnsValue_whenKeyExists() {
        cache.put(1, 10);
        assertEquals(10, cache.get(1));
    }

    @Test
    void get_returnsNull_whenKeyAbsent() {
        assertNull(cache.get(99));
    }

    @Test
    void put_updatesValue_whenKeyAlreadyExists() {
        cache.put(1, 10);
        cache.put(1, 20);
        assertEquals(20, cache.get(1));
        assertEquals(1, cache.size());
    }

    @Test
    void put_evictsLeastRecentlyUsed_whenCapacityExceeded() {
        cache.put(1, 1);
        cache.put(2, 2);
        cache.put(3, 3);  // evicts key 1
        assertNull(cache.get(1));
        assertEquals(2, cache.get(2));
        assertEquals(3, cache.get(3));
    }

    @Test
    void get_refreshesAccessOrder_preventingEviction() {
        cache.put(1, 1);
        cache.put(2, 2);
        cache.get(1);     // access key 1 — key 2 becomes LRU
        cache.put(3, 3);  // evicts key 2
        assertEquals(1, cache.get(1));
        assertNull(cache.get(2));
        assertEquals(3, cache.get(3));
    }

    @Test
    void size_reflectsCurrentEntryCount() {
        assertEquals(0, cache.size());
        cache.put(1, 1);
        assertEquals(1, cache.size());
        cache.put(2, 2);
        assertEquals(2, cache.size());
    }

    @Test
    void clear_removesAllEntries() {
        cache.put(1, 1);
        cache.put(2, 2);
        cache.clear();
        assertEquals(0, cache.size());
        assertNull(cache.get(1));
    }

    @Test
    void get_throwsNullPointerException_forNullKey() {
        assertThrows(NullPointerException.class, () -> cache.get(null));
    }

    @Test
    void put_throwsNullPointerException_forNullKey() {
        assertThrows(NullPointerException.class, () -> cache.put(null, 1));
    }

    @Test
    void put_throwsNullPointerException_forNullValue() {
        assertThrows(NullPointerException.class, () -> cache.put(1, null));
    }
}
