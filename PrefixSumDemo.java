import java.util.List;

/**
 * Demo class for PrefixSum computation.
 */
public class PrefixSumDemo {

    private static final int[] SAMPLE_ARRAY = {10, 20, 10, 5, 15};
    private static final int[] ANOTHER_ARRAY = {5, 5, 5, 5};

    public static void main(String[] args) {
        System.out.println("=== Static Method Demo ===");
        List<Long> result = PrefixSum.computePrefixSum(SAMPLE_ARRAY);
        System.out.println("Prefix sum array: " + result);

        System.out.println("\n=== Builder Pattern Demo (No Cache) ===");
        PrefixSum calculator1 = PrefixSum.builder().build();
        List<Long> result1 = calculator1.compute(SAMPLE_ARRAY);
        System.out.println("Computed result: " + result1);
        System.out.println("Cached result: " + calculator1.getCachedResult());

        System.out.println("\n=== Builder Pattern Demo (With Cache) ===");
        PrefixSum calculator2 = PrefixSum.builder().withCache().build();
        List<Long> result2 = calculator2.compute(SAMPLE_ARRAY);
        System.out.println("Computed result: " + result2);
        System.out.println("Cached result: " + calculator2.getCachedResult());

        System.out.println("\n=== Builder Pattern Demo (With Cache Clear) ===");
        PrefixSum calculator3 = PrefixSum.builder().withCache().withCacheClear().build();
        List<Long> result3a = calculator3.compute(SAMPLE_ARRAY);
        System.out.println("First computation: " + result3a);
        List<Long> result3b = calculator3.compute(ANOTHER_ARRAY);
        System.out.println("Second computation (cache cleared): " + result3b);
        System.out.println("Cached result (only latest): " + calculator3.getCachedResult());
    }
}
