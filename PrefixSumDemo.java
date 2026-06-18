import java.util.List;

/**
 * Demo class for PrefixSum computation.
 */
public class PrefixSumDemo {

    private static final int[] SAMPLE_ARRAY = {10, 20, 10, 5, 15};

    public static void main(String[] args) {
        System.out.println("=== Static Method Demo ===");
        List<Long> result = PrefixSum.computePrefixSum(SAMPLE_ARRAY);
        System.out.println("Prefix sum array: " + result);

        System.out.println("\n=== Instance with Caching Demo ===");
        PrefixSum calculator = new PrefixSum(true);
        List<Long> result2 = calculator.compute(SAMPLE_ARRAY);
        System.out.println("Computed result: " + result2);
        System.out.println("Cached result: " + calculator.getCachedResult());
    }
}
