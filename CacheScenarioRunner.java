public final class CacheScenarioRunner {
    private static final Integer MISSING = -1;

    public void runAll() {
        runEvictionScenario();
        runUpdateScenario();
        runSharedCacheApiScenario();
        runConstructorValidationScenario();
        runNullValidationScenario();
    }

    private void runEvictionScenario() {
        Cache<Integer, Integer> cache = new LRUCache<>(2);

        cache.put(1, 1);
        cache.put(2, 2);
        assertValue("reads most recent entry", cache, 1, 1);

        cache.put(3, 3);
        assertMissing("evicts least recently used entry", cache, 2);

        cache.put(4, 4);
        assertMissing("evicts older entry after another insert", cache, 1);
        assertValue("retains recently used entry", cache, 3, 3);
        assertValue("returns newest entry", cache, 4, 4);
    }

    private void runUpdateScenario() {
        Cache<Integer, Integer> cache = new LRUCache<>(2);

        cache.put(1, 1);
        cache.put(2, 2);
        cache.put(1, 10);
        cache.put(3, 3);

        assertValue("keeps updated value", cache, 1, 10);
        CacheAssertions.assertEquals("does not grow when updating an existing key", 2, cache.size());
        assertMissing("evicts the correct key after an update", cache, 2);
    }

    private void runSharedCacheApiScenario() {
        Cache<String, String> cache = new LRUCache<>(3);

        CacheAssertions.assertEquals("exposes configured capacity", 3, cache.capacity());
        CacheAssertions.assertTrue("starts empty", cache.isEmpty());
        cache.put("a", "alpha");
        cache.put("b", "beta");
        CacheAssertions.assertTrue("reports existing key", cache.containsKey("a"));
        CacheAssertions.assertEquals(
            "returns fallback for missing value",
            "missing",
            cache.getOrDefault("z", "missing")
        );
        CacheAssertions.assertEquals("returns removed value", "alpha", cache.remove("a").orElse("missing"));
        CacheAssertions.assertFalse("removed key is no longer present", cache.containsKey("a"));

        cache.clear();
        CacheAssertions.assertEquals("clears all entries", 0, cache.size());
        CacheAssertions.assertTrue("is empty after clear", cache.isEmpty());
    }

    private void runConstructorValidationScenario() {
        CacheAssertions.assertThrows(
            "zero capacity",
            IllegalArgumentException.class,
            () -> new LRUCache<>(0)
        );
    }

    private void runNullValidationScenario() {
        Cache<Integer, Integer> cache = new LRUCache<>(1);

        CacheAssertions.assertThrows("null key on put", NullPointerException.class, () -> cache.put(null, 1));
        CacheAssertions.assertThrows("null value on put", NullPointerException.class, () -> cache.put(1, null));
        CacheAssertions.assertThrows("null key on get", NullPointerException.class, () -> cache.get(null));
        CacheAssertions.assertThrows(
            "null key on containsKey",
            NullPointerException.class,
            () -> cache.containsKey(null)
        );
        CacheAssertions.assertThrows("null key on remove", NullPointerException.class, () -> cache.remove(null));
    }

    private void assertValue(
        String description,
        Cache<Integer, Integer> cache,
        int key,
        int expectedValue
    ) {
        CacheAssertions.assertEquals(
            description,
            Integer.valueOf(expectedValue),
            cache.get(key).orElse(MISSING)
        );
    }

    private void assertMissing(String description, Cache<Integer, Integer> cache, int key) {
        CacheAssertions.assertFalse(description, cache.get(key).isPresent());
    }
}
