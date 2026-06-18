import java.util.List;

/**
 * Unit tests for PrefixSum computation.
 */
public class PrefixSumTest {

    public static void main(String[] args) {
        testBasicComputation();
        testCaching();
        testCacheClear();
        testEdgeCases();
        System.out.println("\n✓ All tests passed!");
    }

    private static void testBasicComputation() {
        System.out.println("Testing basic computation...");
        int[] arr = {1, 2, 3, 4};
        List<Long> result = PrefixSum.computePrefixSum(arr);
        assert result.equals(List.of(1L, 3L, 6L, 10L)) : "Basic computation failed";
    }

    private static void testCaching() {
        System.out.println("Testing caching functionality...");
        PrefixSum calculator = PrefixSum.builder().withCache().build();
        int[] arr = {10, 20, 30};
        List<Long> result = calculator.compute(arr);
        List<Long> cached = calculator.getCachedResult();
        assert cached.equals(result) : "Caching failed";
    }

    private static void testCacheClear() {
        System.out.println("Testing cache clear on compute...");
        PrefixSum calculator = PrefixSum.builder().withCache().withCacheClear().build();
        calculator.compute(new int[]{1, 2, 3});
        List<Long> firstCache = calculator.getCachedResult();
        calculator.compute(new int[]{5, 5});
        List<Long> secondCache = calculator.getCachedResult();
        assert !firstCache.equals(secondCache) : "Cache clear failed";
    }

    private static void testEdgeCases() {
        System.out.println("Testing edge cases...");
        PrefixSum calculator = PrefixSum.builder().build();

        try {
            calculator.compute(null);
            assert false : "Should throw NullPointerException";
        } catch (NullPointerException e) {
            // Expected
        }

        try {
            calculator.compute(new int[]{});
            assert false : "Should throw IllegalArgumentException";
        } catch (IllegalArgumentException e) {
            // Expected
        }

        List<Long> singleElement = calculator.compute(new int[]{42});
        assert singleElement.equals(List.of(42L)) : "Single element failed";
    }
}
