package prefixsum;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods for computing prefix sums.
 */
public final class PrefixSumArray {

    private PrefixSumArray() {
        // Utility class.
    }

    /**
     * Computes the running total after each element in the input array.
     *
     * @param values the source values
     * @return a list of prefix sums with one entry per input value
     */
    public static List<Integer> prefixSums(int[] values) {
        requireValues(values);

        List<Integer> prefixSums = new ArrayList<>(values.length);
        accumulate(values, (index, runningTotal) -> prefixSums.add(Math.toIntExact(runningTotal)));
        return prefixSums;
    }

    /**
     * Computes the running total after each element in the input array.
     *
     * @param values the source values
     * @return the prefix sums as a primitive array
     */
    public static int[] prefixSumsAsArray(int[] values) {
        requireValues(values);

        int[] prefixSums = new int[values.length];
        accumulate(values, (index, runningTotal) -> prefixSums[index] = Math.toIntExact(runningTotal));
        return prefixSums;
    }

    /**
     * Computes the running total after each element in the input array.
     *
     * @param values the source values
     * @return the prefix sums as a primitive long array
     */
    public static long[] prefixSumsAsLongArray(int[] values) {
        requireValues(values);

        long[] prefixSums = new long[values.length];
        accumulate(values, (index, runningTotal) -> prefixSums[index] = runningTotal);
        return prefixSums;
    }

    private static void accumulate(int[] values, IndexedLongConsumer consumer) {
        long runningTotal = 0L;
        for (int i = 0; i < values.length; i++) {
            runningTotal += values[i];
            consumer.accept(i, runningTotal);
        }
    }

    private static void requireValues(int[] values) {
        if (values == null) {
            throw new IllegalArgumentException("values must not be null");
        }
    }

    @FunctionalInterface
    private interface IndexedLongConsumer {
        void accept(int index, long value);
    }
}
