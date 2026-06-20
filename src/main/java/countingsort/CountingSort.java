package countingsort;

import java.util.Arrays;
import java.util.Objects;

public final class CountingSort {

    private CountingSort() {
        // Utility class.
    }

    /**
     * Returns a sorted copy of the supplied values using counting sort.
     *
     * <p>The input array is never modified.</p>
     */
    public static int[] sort(int[] values) {
        Objects.requireNonNull(values, "values");
        if (values.length < 2) {
            return Arrays.copyOf(values, values.length);
        }

        int min = values[0];
        int max = values[0];
        for (int value : values) {
            if (value < min) {
                min = value;
            } else if (value > max) {
                max = value;
            }
        }

        int[] prefixSums = buildPrefixSums(values, min, max);
        return placeSortedValues(values, prefixSums, min);
    }

    private static int[] buildPrefixSums(int[] values, int min, int max) {
        int[] counts = countValues(values, min, max);
        for (int i = 1; i < counts.length; i++) {
            counts[i] += counts[i - 1];
        }
        return counts;
    }

    private static int[] countValues(int[] values, int min, int max) {
        long range = (long) max - min + 1L;
        if (range > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Input range is too large for counting sort");
        }

        int[] counts = new int[(int) range];
        for (int value : values) {
            counts[value - min]++;
        }
        return counts;
    }

    private static int[] placeSortedValues(int[] values, int[] prefixSums, int min) {
        int[] sorted = new int[values.length];
        for (int i = values.length - 1; i >= 0; i--) {
            int value = values[i];
            sorted[--prefixSums[value - min]] = value;
        }
        return sorted;
    }
}
