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

        Bounds bounds = findBounds(values);
        int[] prefixCounts = buildPrefixCounts(values, bounds.min, bounds.max);
        return placeValues(values, prefixCounts, bounds.min);
    }

    /**
     * Legacy alias retained for callers that still use the original method name.
     */
    @Deprecated
    public static int[] countSort(int[] values) {
        return sort(values);
    }

    private static Bounds findBounds(int[] values) {
        int min = values[0];
        int max = values[0];
        for (int value : values) {
            if (value < min) {
                min = value;
            }
            if (value > max) {
                max = value;
            }
        }
        return new Bounds(min, max);
    }

    private static int[] buildPrefixCounts(int[] values, int min, int max) {
        long range = (long) max - min + 1L;
        if (range > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Input range is too large for counting sort");
        }

        int[] counts = new int[(int) range];
        for (int value : values) {
            counts[value - min]++;
        }

        for (int i = 1; i < counts.length; i++) {
            counts[i] += counts[i - 1];
        }
        return counts;
    }

    private static int[] placeValues(int[] values, int[] prefixCounts, int min) {
        int[] sorted = new int[values.length];
        for (int i = values.length - 1; i >= 0; i--) {
            int value = values[i];
            int countIndex = value - min;
            sorted[--prefixCounts[countIndex]] = value;
        }
        return sorted;
    }

    private static final class Bounds {
        private final int min;
        private final int max;

        private Bounds(int min, int max) {
            this.min = min;
            this.max = max;
        }
    }
}
