public class Main {
    public static void main(String[] args) {
        runEvictionScenario();
        runUpdateScenario();
        runValidationScenario();
        System.out.println("All cache checks passed.");
    }

    private static void runEvictionScenario() {
        LRUCache cache = new LRUCache(2);

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
        LRUCache cache = new LRUCache(2);

        cache.put(1, 1);
        cache.put(2, 2);
        cache.put(1, 10);
        cache.put(3, 3);

        assertValue("keeps updated value", cache, 1, 10);
        assertEquals("does not grow when updating an existing key", 2, cache.size());
        assertMissing("evicts the correct key after an update", cache, 2);
    }

    private static void runValidationScenario() {
        assertThrowsIllegalArgument("zero capacity", 0);
    }

    private static void assertValue(String description, LRUCache cache, int key, int expectedValue) {
        assertEquals(description, expectedValue, cache.get(key));
    }

    private static void assertMissing(String description, LRUCache cache, int key) {
        assertEquals(description, -1, cache.get(key));
    }

    private static void assertThrowsIllegalArgument(String description, int capacity) {
        try {
            new LRUCache(capacity);
            throw new AssertionError("expected IllegalArgumentException for " + description);
        } catch (IllegalArgumentException expected) {
            // Expected path.
        }
    }

    private static void assertEquals(String description, int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError(
                description + ": expected " + expected + ", but got " + actual
            );
        }
    }
}
