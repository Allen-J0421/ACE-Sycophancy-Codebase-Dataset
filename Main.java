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
        assertEquals("reads most recent entry", 1, cache.get(1));

        cache.put(3, 3);
        assertEquals("evicts least recently used entry", -1, cache.get(2));

        cache.put(4, 4);
        assertEquals("evicts older entry after another insert", -1, cache.get(1));
        assertEquals("retains recently used entry", 3, cache.get(3));
        assertEquals("returns newest entry", 4, cache.get(4));
    }

    private static void runUpdateScenario() {
        LRUCache cache = new LRUCache(2);

        cache.put(1, 1);
        cache.put(2, 2);
        cache.put(1, 10);
        cache.put(3, 3);

        assertEquals("keeps updated value", 10, cache.get(1));
        assertEquals("does not grow when updating an existing key", 2, cache.size());
        assertEquals("evicts the correct key after an update", -1, cache.get(2));
    }

    private static void runValidationScenario() {
        try {
            new LRUCache(0);
            throw new AssertionError("expected IllegalArgumentException for zero capacity");
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
