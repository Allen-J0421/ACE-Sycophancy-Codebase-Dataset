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
        return toIntegerList(PrefixSumCalculator.compute(values));
    }

    /**
     * Computes the running total after each element in the input array.
     *
     * @param values the source values
     * @return the prefix sums as a primitive array
     */
    public static int[] prefixSumsAsArray(int[] values) {
        long[] prefixSums = PrefixSumCalculator.compute(values);
        int[] boundedPrefixSums = new int[prefixSums.length];
        for (int i = 0; i < prefixSums.length; i++) {
            boundedPrefixSums[i] = Math.toIntExact(prefixSums[i]);
        }

        return boundedPrefixSums;
    }

    /**
     * Computes the running total after each element in the input array.
     *
     * @param values the source values
     * @return the prefix sums as a primitive long array
     */
    public static long[] prefixSumsAsLongArray(int[] values) {
        return PrefixSumCalculator.compute(values);
    }

    private static List<Integer> toIntegerList(long[] prefixSums) {
        List<Integer> boxedPrefixSums = new ArrayList<>(prefixSums.length);
        for (long prefixSum : prefixSums) {
            boxedPrefixSums.add(Math.toIntExact(prefixSum));
        }
        return boxedPrefixSums;
    }

}
