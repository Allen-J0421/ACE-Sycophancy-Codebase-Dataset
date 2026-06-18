import java.util.Collections;
import java.util.List;

/**
 * Unit tests for PrefixSum computation.
 */
public class PrefixSumTest {

    public static void main(String[] args) {
        testBasicComputation();
        testCaching();
        testCacheClear();
        testStatistics();
        testResultImmutability();
        testEdgeCases();
        System.out.println("\n✓ All tests passed!");
    }

    private static void testBasicComputation() {
        System.out.println("Testing basic computation...");
        int[] arr = {1, 2, 3, 4};
        PrefixSum.PrefixSumResult result = PrefixSum.computePrefixSum(arr);
        assert result.getValues().equals(List.of(1L, 3L, 6L, 10L)) : "Basic computation failed";
        assert result.getTotalSum() == 10L : "Total sum mismatch";
    }

    private static void testCaching() {
        System.out.println("Testing caching functionality...");
        PrefixSum calculator = PrefixSum.builder().withCache().build();
        int[] arr = {10, 20, 30};
        PrefixSum.PrefixSumResult result = calculator.compute(arr);
        List<Long> cached = calculator.getCachedResult();
        assert cached.equals(result.getValues()) : "Caching failed";
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

    private static void testStatistics() {
        System.out.println("Testing statistics...");
        PrefixSum calculator = PrefixSum.builder().build();
        calculator.compute(new int[]{10, 20, 30});
        assert calculator.getComputationCount() == 1 : "Computation count mismatch";
        calculator.compute(new int[]{5, 5, 5});
        assert calculator.getComputationCount() == 2 : "Computation count not incremented";

        PrefixSum.PrefixSumResult result = PrefixSum.computePrefixSum(new int[]{2, 4, 6});
        assert result.getTotalSum() == 12L : "Total sum incorrect";
        assert result.getInputSize() == 3 : "Input size incorrect";
        assert Math.abs(result.getAverage() - 4.0) < 0.01 : "Average incorrect";
    }

    private static void testResultImmutability() {
        System.out.println("Testing result immutability...");
        PrefixSum.PrefixSumResult result = PrefixSum.computePrefixSum(new int[]{1, 2, 3});
        List<Long> values = result.getValues();
        try {
            values.add(100L);
            assert false : "Result should be immutable";
        } catch (UnsupportedOperationException e) {
            // Expected
        }
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

        PrefixSum.PrefixSumResult singleElement = calculator.compute(new int[]{42});
        assert singleElement.getValues().equals(List.of(42L)) : "Single element failed";
        assert singleElement.getTotalSum() == 42L : "Single element sum mismatch";
    }
}
