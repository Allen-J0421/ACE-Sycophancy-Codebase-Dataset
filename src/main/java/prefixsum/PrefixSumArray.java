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
        return toIntegerList(computePrefixSums(values));
    }

    /**
     * Computes the running total after each element in the input array.
     *
     * @param values the source values
     * @return the prefix sums as a primitive array
     */
    public static int[] prefixSumsAsArray(int[] values) {
        return toIntArray(computePrefixSums(values));
    }

    /**
     * Computes the running total after each element in the input array.
     *
     * @param values the source values
     * @return the prefix sums as a primitive long array
     */
    public static long[] prefixSumsAsLongArray(int[] values) {
        return computePrefixSums(values);
    }

    private static long[] computePrefixSums(int[] values) {
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

    private static int[] toIntArray(long[] prefixSums) {
        int[] boundedPrefixSums = new int[prefixSums.length];
        for (int i = 0; i < prefixSums.length; i++) {
            boundedPrefixSums[i] = Math.toIntExact(prefixSums[i]);
        }

        return boundedPrefixSums;
    }

    private static List<Integer> toIntegerList(long[] prefixSums) {
        List<Integer> boxedPrefixSums = new ArrayList<>(prefixSums.length);
        for (long prefixSum : prefixSums) {
            boxedPrefixSums.add(Math.toIntExact(prefixSum));
        }
        return boxedPrefixSums;
    }
}
