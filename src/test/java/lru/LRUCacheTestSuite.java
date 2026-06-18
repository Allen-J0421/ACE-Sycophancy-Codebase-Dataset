package lru;

public final class LRUCacheTestSuite {
    public static void main(String[] args) {
        LRUCacheBehaviorTest.main(args);
        LRUCacheCapacityTest.main(args);
        LRUCacheNullSafetyTest.main(args);
        System.out.println("All LRUCache tests passed.");
    }

    private LRUCacheTestSuite() {
        // No instances.
    }
}
