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

        Range range = findRange(values);
        int[] positions = buildPositions(values, range);
        return writeSorted(values, positions, range.min);
    }

    private static Range findRange(int[] values) {
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
        return new Range(min, max);
    }

    private static int[] buildPositions(int[] values, Range range) {
        int[] counts = buildCounts(values, range.min, range.max);
        accumulateCounts(counts);
        return counts;
    }

    private static int[] buildCounts(int[] values, int min, int max) {
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

    private static void accumulateCounts(int[] counts) {
        for (int i = 1; i < counts.length; i++) {
            counts[i] += counts[i - 1];
        }
    }

    private static int[] writeSorted(int[] values, int[] positions, int min) {
        int[] sorted = new int[values.length];
        for (int i = values.length - 1; i >= 0; i--) {
            int value = values[i];
            sorted[--positions[value - min]] = value;
        }
        return sorted;
    }

    private static final class Range {
        private final int min;
        private final int max;

        private Range(int min, int max) {
            this.min = min;
            this.max = max;
        }
    }
}
