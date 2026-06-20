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

        int[] cumulativeCounts = buildCumulativeCounts(values, min, max);
        return placeSortedValues(values, cumulativeCounts, min);
    }

    private static int[] buildCumulativeCounts(int[] values, int min, int max) {
        int[] histogram = buildHistogram(values, min, max);
        for (int i = 1; i < histogram.length; i++) {
            histogram[i] += histogram[i - 1];
        }
        return histogram;
    }

    private static int[] buildHistogram(int[] values, int min, int max) {
        long range = (long) max - min + 1L;
        if (range > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Input range is too large for counting sort");
        }

        int[] histogram = new int[(int) range];
        for (int value : values) {
            histogram[value - min]++;
        }
        return histogram;
    }

    private static int[] placeSortedValues(int[] values, int[] cumulativeCounts, int min) {
        int[] sorted = new int[values.length];
        for (int i = values.length - 1; i >= 0; i--) {
            int value = values[i];
            sorted[--cumulativeCounts[value - min]] = value;
        }
        return sorted;
    }
}
