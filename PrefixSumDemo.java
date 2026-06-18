/**
 * Demo class for PrefixSum computation.
 */
public class PrefixSumDemo {

    private static final int[] SAMPLE_ARRAY = {10, 20, 10, 5, 15};
    private static final int[] ANOTHER_ARRAY = {5, 5, 5, 5};
    private static final int[] LARGE_ARRAY = createLargeArray(1000);

    public static void main(String[] args) {
        System.out.println("=== Static Method Demo ===");
        PrefixSum.PrefixSumResult result = PrefixSum.computePrefixSum(SAMPLE_ARRAY);
        System.out.println("Result: " + result);

        System.out.println("\n=== Strategy Pattern Demo (Default Iterative Strategy) ===");
        PrefixSum calculator1 = new PrefixSum();
        PrefixSum.PrefixSumResult result1 = calculator1.compute(SAMPLE_ARRAY);
        System.out.println("Strategy: " + calculator1.getStrategyName());
        System.out.println("Computed result: " + result1);
        System.out.println("Metrics: " + calculator1.getMetrics());

        System.out.println("\n=== Listener Pattern Demo ===");
        PrefixSumConfig config2 = PrefixSumConfig.builder().withMetrics().build();
        PrefixSum calculator2 = new PrefixSum(config2, new IterativeStrategy());
        calculator2.addListener(new LoggingListener());
        PrefixSum.PrefixSumResult result2 = calculator2.compute(SAMPLE_ARRAY);
        System.out.println("Result values: " + result2.getValues());

        System.out.println("\n=== Config-based Demo (With Cache + Metrics + Listeners) ===");
        PrefixSumConfig config3 = PrefixSumConfig.builder()
            .withCache()
            .withCacheClear()
            .withMetrics()
            .build();
        PrefixSum calculator3 = new PrefixSum(config3);
        calculator3.addListener(new LoggingListener());
        PrefixSum.PrefixSumResult result3a = calculator3.compute(SAMPLE_ARRAY);
        System.out.println("Cached after first computation: " + calculator3.getCachedResult());
        PrefixSum.PrefixSumResult result3b = calculator3.compute(ANOTHER_ARRAY);
        System.out.println("Cached after second computation (cleared): " + calculator3.getCachedResult());

        System.out.println("\n=== Performance Demo (Large Array with Metrics) ===");
        PrefixSumConfig config4 = PrefixSumConfig.builder().withMetrics().build();
        PrefixSum calculator4 = new PrefixSum(config4, new IterativeStrategy());
        PrefixSum.PrefixSumResult result4 = calculator4.compute(LARGE_ARRAY);
        System.out.println("Large array result: size=" + result4.getInputSize() +
                ", totalSum=" + result4.getTotalSum() +
                ", average=" + String.format("%.2f", result4.getAverage()));
        System.out.println("Performance metrics: " + calculator4.getMetrics());
    }

    private static int[] createLargeArray(int size) {
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = i % 100 + 1;
        }
        return arr;
    }
}
