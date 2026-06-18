package prefixsum;

/**
 * Computes prefix sums over primitive integer inputs.
 */
final class PrefixSumCalculator {

    private PrefixSumCalculator() {
        // Utility class.
    }

    static long[] compute(int[] values) {
        requireValues(values);

        long[] prefixSums = new long[values.length];
        long runningTotal = 0L;
        for (int i = 0; i < values.length; i++) {
            runningTotal += values[i];
            prefixSums[i] = runningTotal;
        }

        return prefixSums;
    }

    private static void requireValues(int[] values) {
        if (values == null) {
            throw new IllegalArgumentException("values must not be null");
        }
    }
}
