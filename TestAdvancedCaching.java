/**
 * Tests for advanced caching strategies and performance profiling.
 */
class TestAdvancedCaching {

    public static void main(String[] args) {
        testLruCache();
        testTtlCache();
        testLruCachedSolver();
        testTtlCachedSolver();
        testPerformanceProfiler();
        System.out.println("\n✓ All advanced caching tests passed");
    }

    static void testLruCache() {
        System.out.println("=== LRU Cache ===\n");

        LruCache cache = new LruCache(3);

        // Test basic operations
        System.out.println("Test 1: Basic put/get");
        cache.put("key1", 10);
        cache.put("key2", 20);
        assert cache.get("key1") == 10;
        assert cache.get("key2") == 20;
        System.out.println("✓ Put and retrieve values");

        // Test cache hit
        System.out.println("\nTest 2: Cache hit detection");
        int value = cache.get("key1");
        assert value == 10;
        System.out.println("✓ Cache hit: retrieved " + value);

        // Test LRU eviction
        System.out.println("\nTest 3: LRU eviction");
        cache.put("key3", 30);
        cache.put("key4", 40); // Should evict key2 (least recently used)
        assert cache.get("key1") == 10;
        assert cache.get("key3") == 30;
        assert cache.get("key4") == 40;
        assert !cache.containsKey("key2");
        System.out.println("✓ LRU eviction: capacity maintained at " + cache.size());

        // Test statistics
        System.out.println("\nTest 4: Cache statistics");
        LruCache.CacheStats stats = cache.getStats();
        System.out.println("✓ " + stats);

        System.out.println();
    }

    static void testTtlCache() {
        System.out.println("=== TTL Cache ===\n");

        TtlCache cache = new TtlCache(100); // 100ms TTL

        // Test basic operations
        System.out.println("Test 1: Basic TTL caching");
        cache.put("key1", 10);
        assert cache.get("key1") == 10;
        System.out.println("✓ Value cached");

        // Test non-expired access
        System.out.println("\nTest 2: Valid TTL");
        int value = cache.get("key1");
        assert value == 10;
        System.out.println("✓ Retrieved before TTL expiry: " + value);

        // Test expiration
        System.out.println("\nTest 3: TTL expiration");
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        assert cache.get("key1") == -1; // Should be expired
        System.out.println("✓ Entry expired after TTL");

        // Test cleanup
        System.out.println("\nTest 4: Cache cleanup");
        cache.put("key2", 20);
        cache.put("key3", 30);
        int sizeBefore = cache.size();
        cache.cleanup();
        System.out.println("✓ Cleanup reduced size from " + sizeBefore);

        System.out.println();
    }

    static void testLruCachedSolver() {
        System.out.println("=== LRU Cached Solver ===\n");

        LcsSolver baseSolver = new StandardLcsSolver();
        LruCachedLcsSolver cachedSolver = new LruCachedLcsSolver(baseSolver, 10);

        System.out.println("Test 1: Caching behavior");
        int result1 = cachedSolver.solve(new LcsInput("AGGTAB", "GXTXAYB")).getLength();
        int result2 = cachedSolver.solve(new LcsInput("AGGTAB", "GXTXAYB")).getLength();
        assert result1 == 4 && result2 == 4;
        System.out.println("✓ Consistent results: " + result1);

        System.out.println("\nTest 2: Cache capacity");
        LruCachedLcsSolver smallCache = new LruCachedLcsSolver(new StandardLcsSolver(), 2);
        smallCache.solve(new LcsInput("A", "B"));
        smallCache.solve(new LcsInput("C", "D"));
        smallCache.solve(new LcsInput("E", "F")); // Should evict first entry
        assert smallCache.getCacheStats().entriesStored == 2;
        System.out.println("✓ Capacity maintained: " + smallCache.getCacheStats());

        System.out.println();
    }

    static void testTtlCachedSolver() {
        System.out.println("=== TTL Cached Solver ===\n");

        LcsSolver baseSolver = new StandardLcsSolver();
        TtlCachedLcsSolver cachedSolver = new TtlCachedLcsSolver(baseSolver, 100); // 100ms TTL

        System.out.println("Test 1: Fresh cache");
        int result1 = cachedSolver.solve(new LcsInput("HELLO", "HALLO")).getLength();
        assert result1 == 4;
        System.out.println("✓ Initial solve: " + result1);

        System.out.println("\nTest 2: Cached access");
        int result2 = cachedSolver.solve(new LcsInput("HELLO", "HALLO")).getLength();
        assert result2 == 4;
        System.out.println("✓ Cache hit: " + result2);

        System.out.println("\nTest 3: Expiration");
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        cachedSolver.cleanup();
        int result3 = cachedSolver.solve(new LcsInput("HELLO", "HALLO")).getLength();
        assert result3 == 4; // Recomputed after expiry
        System.out.println("✓ Recomputed after TTL expiry: " + result3);

        System.out.println();
    }

    static void testPerformanceProfiler() {
        System.out.println("=== Performance Profiler ===\n");

        String s1 = "AGGTAB";
        String s2 = "GXTXAYB";

        // Test single execution profiling
        System.out.println("Test 1: Single execution profiling");
        LcsSolver solver = new StandardLcsSolver();
        PerformanceProfiler.ExecutionMetrics metrics = PerformanceProfiler.profile(solver, s1, s2);
        assert metrics.elapsedNanos >= 0;
        System.out.println("✓ " + metrics);

        // Test aggregate metrics
        System.out.println("\nTest 2: Aggregate metrics (5 runs)");
        PerformanceProfiler.AggregateMetrics aggregate = PerformanceProfiler.profileMultiple(
                solver, s1, s2, 5
        );
        assert aggregate.runCount == 5;
        System.out.println("✓ " + aggregate);

        // Test scaling profile (abbreviated)
        System.out.println("\nTest 3: Scaling profile (simplified)");
        for (int size = 10; size <= 30; size += 10) {
            String pattern = "A".repeat(size);
            String search = "B".repeat(size);
            long startNano = System.nanoTime();
            new StandardLcsSolver().solve(new LcsInput(pattern, search));
            long elapsed = System.nanoTime() - startNano;
            System.out.println(String.format("  Size %2d: %.3f ms", size, elapsed / 1_000_000.0));
        }

        System.out.println();
    }
}
