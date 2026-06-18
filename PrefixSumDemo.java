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

        System.out.println("\n=== Factory Pattern Demo (Simple) ===");
        PrefixSum calculator1 = PrefixSumFactory.createSimple();
        PrefixSum.PrefixSumResult result1 = calculator1.compute(SAMPLE_ARRAY);
        System.out.println("Strategy: " + calculator1.getStrategyName());
        System.out.println("Result: " + result1.getValues());

        System.out.println("\n=== Factory Pattern Demo (Cached) ===");
        PrefixSum calculator2 = PrefixSumFactory.createCached();
        PrefixSum.PrefixSumResult result2 = calculator2.compute(SAMPLE_ARRAY);
        System.out.println("Cached result: " + calculator2.getCachedResult());

        System.out.println("\n=== Factory Pattern Demo (Monitored) ===");
        PrefixSum calculator3 = PrefixSumFactory.createMonitored();
        PrefixSum.PrefixSumResult result3 = calculator3.compute(SAMPLE_ARRAY);
        System.out.println("Metrics: " + calculator3.getMetrics());

        System.out.println("\n=== Template Method + Listener Demo ===");
        PrefixSum calculator4 = PrefixSumFactory.createSimple();
        calculator4.addListener(new LoggingListener());
        calculator4.addListener(new TimingListener());
        PrefixSum.PrefixSumResult result4 = calculator4.compute(SAMPLE_ARRAY);
        System.out.println("Result values: " + result4.getValues());

        System.out.println("\n=== Composite Listener Demo ===");
        PrefixSum calculator5 = new PrefixSum();
        CompositeListener composite = new CompositeListener()
            .add(new LoggingListener())
            .add(new TimingListener());
        calculator5.addListener(composite);
        PrefixSum.PrefixSumResult result5 = calculator5.compute(SAMPLE_ARRAY);
        System.out.println("Result: " + result5.getValues());

        System.out.println("\n=== Factory Pattern Demo (Full Featured) ===");
        PrefixSum calculator6 = PrefixSumFactory.createFull();
        PrefixSum.PrefixSumResult result6a = calculator6.compute(SAMPLE_ARRAY);
        System.out.println("First computation cached: " + calculator6.getCachedResult());
        PrefixSum.PrefixSumResult result6b = calculator6.compute(ANOTHER_ARRAY);
        System.out.println("Second computation cached: " + calculator6.getCachedResult());
        System.out.println("Overall metrics: " + calculator6.getMetrics());

        System.out.println("\n=== Factory with Strategy Demo ===");
        PrefixSum calculator7 = PrefixSumFactory.createWithStrategy(new IterativeStrategy());
        PrefixSum.PrefixSumResult result7 = calculator7.compute(LARGE_ARRAY);
        System.out.println("Large array: size=" + result7.getInputSize() +
                ", sum=" + result7.getTotalSum() +
                ", avg=" + String.format("%.2f", result7.getAverage()));
    }

    private static int[] createLargeArray(int size) {
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = i % 100 + 1;
        }
        return arr;
    }
}
