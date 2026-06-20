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

        SortPlan plan = buildSortPlan(values);
        return placeValues(values, plan);
    }

    private static SortPlan buildSortPlan(int[] values) {
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

        int[] cumulativeCounts = buildCumulativeCounts(values, min, max);
        return new SortPlan(min, cumulativeCounts);
    }

    private static int[] buildCumulativeCounts(int[] values, int min, int max) {
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

    private static int[] placeValues(int[] values, SortPlan plan) {
        int[] sorted = new int[values.length];
        for (int i = values.length - 1; i >= 0; i--) {
            int value = values[i];
            int countIndex = value - plan.min;
            sorted[--plan.cumulativeCounts[countIndex]] = value;
        }
        return sorted;
    }

    private static final class SortPlan {
        private final int min;
        private final int[] cumulativeCounts;

        private SortPlan(int min, int[] cumulativeCounts) {
            this.min = min;
            this.cumulativeCounts = cumulativeCounts;
        }
    }
}
