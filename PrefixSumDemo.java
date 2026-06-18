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

        System.out.println("\n=== Fluent Builder Demo (Simple) ===");
        PrefixSum calculator1 = new PrefixSumBuilder()
            .withLogging()
            .build();
        PrefixSum.PrefixSumResult result1 = calculator1.compute(SAMPLE_ARRAY);
        System.out.println("Result: " + result1.getValues());

        System.out.println("\n=== Fluent Builder Demo (Cached + Metrics) ===");
        PrefixSum calculator2 = new PrefixSumBuilder()
            .withCacheClear()
            .withMetrics()
            .withLogging()
            .withTiming()
            .build();
        PrefixSum.PrefixSumResult result2a = calculator2.compute(SAMPLE_ARRAY);
        PrefixSum.PrefixSumResult result2b = calculator2.compute(ANOTHER_ARRAY);
        System.out.println("Metrics: " + calculator2.getMetrics());

        System.out.println("\n=== Lambda Listener Demo ===");
        PrefixSum calculator3 = new PrefixSumBuilder()
            .withLambdaListener(new LambdaListener.Builder()
                .onStart(size -> System.out.println("[LAMBDA] Computing for " + size + " elements"))
                .onComplete(result3 -> System.out.println("[LAMBDA] Sum = " + result3.getTotalSum()))
                .build())
            .build();
        PrefixSum.PrefixSumResult result3 = calculator3.compute(SAMPLE_ARRAY);

        System.out.println("\n=== Decorator Strategy Demo (Caching) ===");
        CachingStrategy cachingStrategy = new CachingStrategy(new IterativeStrategy());
        PrefixSum calculator4 = new PrefixSum(PrefixSumConfig.defaults(), cachingStrategy);
        System.out.println("Strategy: " + calculator4.getStrategyName());
        PrefixSum.PrefixSumResult result4a = calculator4.compute(SAMPLE_ARRAY);
        System.out.println("First call - Cache size: " + cachingStrategy.getCacheSize());
        PrefixSum.PrefixSumResult result4b = calculator4.compute(SAMPLE_ARRAY);
        System.out.println("Second call (cached) - Cache size: " + cachingStrategy.getCacheSize());
        System.out.println("Results equal: " + result4a.getValues().equals(result4b.getValues()));

        System.out.println("\n=== Decorator Strategy Demo (Validation) ===");
        ValidationStrategy validatingStrategy = new ValidationStrategy(new IterativeStrategy());
        PrefixSum calculator5 = new PrefixSum(PrefixSumConfig.defaults(), validatingStrategy);
        System.out.println("Strategy: " + calculator5.getStrategyName());
        PrefixSum.PrefixSumResult result5 = calculator5.compute(SAMPLE_ARRAY);
        System.out.println("Result: " + result5.getValues());

        System.out.println("\n=== Decorator Chain Demo (Validation + Caching) ===");
        ComputationStrategy chain = new CachingStrategy(new ValidationStrategy(new IterativeStrategy()));
        PrefixSum calculator6 = new PrefixSum(
            PrefixSumConfig.builder().withMetrics().build(),
            chain
        );
        calculator6.addListener(new LoggingListener());
        System.out.println("Strategy: " + calculator6.getStrategyName());
        PrefixSum.PrefixSumResult result6 = calculator6.compute(SAMPLE_ARRAY);
        System.out.println("Result: " + result6.getValues());

        System.out.println("\n=== Complex Builder Demo ===");
        PrefixSum calculator7 = new PrefixSumBuilder()
            .withAllFeatures()
            .withStrategy(new CachingStrategy(new IterativeStrategy()))
            .withLogging()
            .withTiming()
            .build();
        PrefixSum.PrefixSumResult result7 = calculator7.compute(LARGE_ARRAY);
        System.out.println("Large array: size=" + result7.getInputSize() +
                ", sum=" + result7.getTotalSum());
    }

    private static int[] createLargeArray(int size) {
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = i % 100 + 1;
        }
        return arr;
    }
}
