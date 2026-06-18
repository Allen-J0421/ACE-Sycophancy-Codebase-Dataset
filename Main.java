public class Main {
    private static final Integer MISSING = -1;

    public static void main(String[] args) {
        runEvictionScenario();
        runUpdateScenario();
        runSharedCacheApiScenario();
        runConstructorValidationScenario();
        runNullValidationScenario();
        System.out.println("All cache checks passed.");
    }

    private static void runEvictionScenario() {
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

    private static void runUpdateScenario() {
        Cache<Integer, Integer> cache = new LRUCache<>(2);

        cache.put(1, 1);
        cache.put(2, 2);
        cache.put(1, 10);
        cache.put(3, 3);

        assertValue("keeps updated value", cache, 1, 10);
        assertEquals("does not grow when updating an existing key", 2, cache.size());
        assertMissing("evicts the correct key after an update", cache, 2);
    }

    private static void runSharedCacheApiScenario() {
        Cache<String, String> cache = new LRUCache<>(3);

        assertEquals("exposes configured capacity", 3, cache.capacity());
        assertTrue("starts empty", cache.isEmpty());
        cache.put("a", "alpha");
        cache.put("b", "beta");
        assertTrue("reports existing key", cache.containsKey("a"));
        assertEquals("returns fallback for missing value", "missing", cache.getOrDefault("z", "missing"));
        assertEquals("returns removed value", "alpha", cache.remove("a").orElse("missing"));
        assertFalse("removed key is no longer present", cache.containsKey("a"));

        cache.clear();
        assertEquals("clears all entries", 0, cache.size());
        assertTrue("is empty after clear", cache.isEmpty());
    }

    private static void runConstructorValidationScenario() {
        assertThrowsIllegalArgument("zero capacity", 0);
    }

    private static void runNullValidationScenario() {
        Cache<Integer, Integer> cache = new LRUCache<>(1);

        assertThrowsNullPointer("null key on put", () -> cache.put(null, 1));
        assertThrowsNullPointer("null value on put", () -> cache.put(1, null));
        assertThrowsNullPointer("null key on get", () -> cache.get(null));
        assertThrowsNullPointer("null key on containsKey", () -> cache.containsKey(null));
        assertThrowsNullPointer("null key on remove", () -> cache.remove(null));
    }

    private static void assertValue(
        String description,
        Cache<Integer, Integer> cache,
        int key,
        int expectedValue
    ) {
        assertEquals(description, Integer.valueOf(expectedValue), cache.get(key).orElse(MISSING));
    }

    private static void assertMissing(String description, Cache<Integer, Integer> cache, int key) {
        assertFalse(description, cache.get(key).isPresent());
    }

    private static void assertThrowsIllegalArgument(String description, int capacity) {
        try {
            new LRUCache<>(capacity);
            throw new AssertionError("expected IllegalArgumentException for " + description);
        } catch (IllegalArgumentException expected) {
            // Expected path.
        }
    }

    private static void assertThrowsNullPointer(String description, ThrowingRunnable action) {
        try {
            action.run();
            throw new AssertionError("expected NullPointerException for " + description);
        } catch (NullPointerException expected) {
            // Expected path.
        }
    }

    private static void assertTrue(String description, boolean condition) {
        if (!condition) {
            throw new AssertionError(description);
        }
    }

    private static void assertFalse(String description, boolean condition) {
        if (condition) {
            throw new AssertionError(description);
        }
    }

    private static void assertEquals(String description, Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError(
                description + ": expected " + expected + ", but got " + actual
            );
        }
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run();
    }
}
