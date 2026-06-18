import java.util.List;

/**
 * Unit tests for PrefixSum computation.
 */
public class PrefixSumTest {

    public static void main(String[] args) {
        testBasicComputation();
        testStrategyPattern();
        testConfigBasedCaching();
        testConfigBasedMetrics();
        testListenerPattern();
        testAbstractListener();
        testCompositeListener();
        testFactoryPattern();
        testInputValidator();
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

    private static void testStrategyPattern() {
        System.out.println("Testing strategy pattern...");
        ComputationStrategy strategy = new IterativeStrategy();
        PrefixSum calculator = new PrefixSum(PrefixSumConfig.defaults(), strategy);
        PrefixSum.PrefixSumResult result = calculator.compute(new int[]{1, 2, 3});
        assert "Iterative".equals(calculator.getStrategyName()) : "Strategy name mismatch";
        assert result.getValues().equals(List.of(1L, 3L, 6L)) : "Strategy computation failed";
    }

    private static void testConfigBasedCaching() {
        System.out.println("Testing config-based caching...");
        PrefixSumConfig config = PrefixSumConfig.builder().withCache().build();
        PrefixSum calculator = new PrefixSum(config);
        int[] arr = {10, 20, 30};
        PrefixSum.PrefixSumResult result = calculator.compute(arr);
        List<Long> cached = calculator.getCachedResult();
        assert cached.equals(result.getValues()) : "Caching failed";
    }

    private static void testConfigBasedMetrics() {
        System.out.println("Testing config-based metrics...");
        PrefixSumConfig config = PrefixSumConfig.builder().withMetrics().build();
        PrefixSum calculator = new PrefixSum(config);
        calculator.compute(new int[]{1, 2, 3});
        ComputationMetrics metrics = calculator.getMetrics();
        assert metrics.getComputationCount() == 1 : "Metrics computation count mismatch";
        assert metrics.getComputationTimeMs() >= 0 : "Computation time should be non-negative";
    }

    private static void testListenerPattern() {
        System.out.println("Testing listener pattern...");
        TestListener listener = new TestListener();
        PrefixSum calculator = new PrefixSum();
        calculator.addListener(listener);
        calculator.compute(new int[]{1, 2, 3});
        assert listener.startCalled : "onComputationStart not called";
        assert listener.completeCalled : "onComputationComplete not called";
        assert !listener.errorCalled : "onComputationError should not be called";
    }

    private static void testAbstractListener() {
        System.out.println("Testing abstract listener...");
        TestAbstractListener listener = new TestAbstractListener();
        PrefixSum calculator = new PrefixSum();
        calculator.addListener(listener);
        calculator.compute(new int[]{1, 2, 3});
        assert listener.handleStartCalled : "handleComputationStart not called";
        assert listener.handleCompleteCalled : "handleComputationComplete not called";
    }

    private static void testCompositeListener() {
        System.out.println("Testing composite listener...");
        TestListener listener1 = new TestListener();
        TestListener listener2 = new TestListener();
        CompositeListener composite = new CompositeListener()
            .add(listener1)
            .add(listener2);
        PrefixSum calculator = new PrefixSum();
        calculator.addListener(composite);
        calculator.compute(new int[]{1, 2, 3});
        assert listener1.startCalled : "First listener not called";
        assert listener2.startCalled : "Second listener not called";
    }

    private static void testFactoryPattern() {
        System.out.println("Testing factory pattern...");
        PrefixSum simple = PrefixSumFactory.createSimple();
        assert simple != null : "Factory should not return null";

        PrefixSum cached = PrefixSumFactory.createCached();
        cached.compute(new int[]{1, 2, 3});
        assert !cached.getCachedResult().isEmpty() : "Cached result should not be empty";

        PrefixSum monitored = PrefixSumFactory.createMonitored();
        monitored.compute(new int[]{1, 2, 3});
        assert monitored.getMetrics().getComputationCount() > 0 : "Metrics should be tracked";

        PrefixSum full = PrefixSumFactory.createFull();
        full.compute(new int[]{1, 2, 3});
        assert !full.getCachedResult().isEmpty() : "Full calculator should cache";
        assert full.getMetrics().getComputationCount() > 0 : "Full calculator should track metrics";
    }

    private static void testInputValidator() {
        System.out.println("Testing input validator...");
        assert InputValidator.isValid(new int[]{1, 2, 3}) : "Valid array check failed";
        assert !InputValidator.isValid(null) : "Null array check failed";
        assert !InputValidator.isValid(new int[]{}) : "Empty array check failed";
    }

    private static void testCacheClear() {
        System.out.println("Testing cache clear on compute...");
        PrefixSumConfig config = PrefixSumConfig.builder()
            .withCache()
            .withCacheClear()
            .build();
        PrefixSum calculator = new PrefixSum(config);
        calculator.compute(new int[]{1, 2, 3});
        List<Long> firstCache = calculator.getCachedResult();
        calculator.compute(new int[]{5, 5});
        List<Long> secondCache = calculator.getCachedResult();
        assert !firstCache.equals(secondCache) : "Cache clear failed";
    }

    private static void testStatistics() {
        System.out.println("Testing statistics...");
        PrefixSum calculator = new PrefixSum();
        calculator.compute(new int[]{10, 20, 30});
        assert calculator.getMetrics().getComputationCount() == 1 : "Computation count mismatch";
        calculator.compute(new int[]{5, 5, 5});
        assert calculator.getMetrics().getComputationCount() == 2 : "Computation count not incremented";

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
        PrefixSum calculator = new PrefixSum();

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

        try {
            new PrefixSum(null);
            assert false : "Should throw NullPointerException for null config";
        } catch (NullPointerException e) {
            // Expected
        }

        try {
            new PrefixSum(PrefixSumConfig.defaults(), null);
            assert false : "Should throw NullPointerException for null strategy";
        } catch (NullPointerException e) {
            // Expected
        }
    }

    static class TestListener implements ComputationListener {
        boolean startCalled = false;
        boolean completeCalled = false;
        boolean errorCalled = false;

        @Override
        public void onComputationStart(int arraySize) {
            startCalled = true;
        }

        @Override
        public void onComputationComplete(PrefixSum.PrefixSumResult result) {
            completeCalled = true;
        }

        @Override
        public void onComputationError(Exception exception) {
            errorCalled = true;
        }
    }

    static class TestAbstractListener extends AbstractListener {
        boolean handleStartCalled = false;
        boolean handleCompleteCalled = false;

        @Override
        protected void handleComputationStart(int arraySize) {
            handleStartCalled = true;
        }

        @Override
        protected void handleComputationComplete(PrefixSum.PrefixSumResult result) {
            handleCompleteCalled = true;
        }

        @Override
        protected void handleComputationError(Exception exception) {
            // No op
        }
    }
}
