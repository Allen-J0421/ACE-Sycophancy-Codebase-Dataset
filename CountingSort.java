import java.util.Objects;

/**
 * Stable counting sort for integer arrays.
 */
public final class CountingSort {

    private CountingSort() {
    }

    /**
     * Legacy wrapper retained for compatibility.
     */
    public static int[] countSort(int[] values) {
        return sortedCopy(values);
    }

    /**
     * Legacy wrapper retained for compatibility.
     */
    public static int[] sort(int[] values) {
        return sortedCopy(values);
    }

    /**
     * Returns a sorted copy of {@code values}.
     */
    public static int[] sortedCopy(int[] values) {
        Objects.requireNonNull(values, "values");
        int[] sortedValues = new int[values.length];
        copyInto(values, sortedValues);
        return sortedValues;
    }

    /**
     * Writes the sorted contents of {@code input} into {@code output}.
     */
    public static void copyInto(int[] input, int[] output) {
        Objects.requireNonNull(input, "input");
        Objects.requireNonNull(output, "output");
        requireSameLength(input, output);

        if (input.length == 0) {
            return;
        }

        int[] source = input == output ? input.clone() : input;
        SortState sortState = buildSortState(source);
        populateSortedValues(source, output, sortState.prefixCounts(), sortState.range());
    }

    /**
     * Sorts {@code values} in place.
     */
    public static void sortInPlace(int[] values) {
        Objects.requireNonNull(values, "values");

        if (values.length == 0) {
            return;
        }

        copyInto(values, values);
    }

    private static SortState buildSortState(int[] values) {
        Range range = findRange(values);
        int[] prefixCounts = buildPrefixCounts(values, range);
        return new SortState(range, prefixCounts);
    }

    private static void requireSameLength(int[] input, int[] output) {
        if (input.length != output.length) {
            throw new IllegalArgumentException(
                "Input and output arrays must have the same length."
            );
        }
    }

    private static Range findRange(int[] values) {
        int minValue = values[0];
        int maxValue = values[0];

        for (int i = 1; i < values.length; i++) {
            int value = values[i];
            if (value < minValue) {
                minValue = value;
            }
            if (value > maxValue) {
                maxValue = value;
            }
        }

        return new Range(minValue, maxValue);
    }

    private static int[] buildPrefixCounts(int[] values, Range range) {
        int[] counts = new int[range.size()];

        for (int value : values) {
            counts[range.indexOf(value)]++;
        }

        for (int i = 1; i < counts.length; i++) {
            counts[i] += counts[i - 1];
        }

        return counts;
    }

    private static void populateSortedValues(
        int[] values,
        int[] output,
        int[] prefixCounts,
        Range range
    ) {
        for (int i = values.length - 1; i >= 0; i--) {
            int value = values[i];
            int countIndex = range.indexOf(value);
            output[prefixCounts[countIndex] - 1] = value;
            prefixCounts[countIndex]--;
        }
    }

    private record SortState(Range range, int[] prefixCounts) {
    }

    private record Range(int minValue, int maxValue) {
        private int size() {
            long size = (long) maxValue - minValue + 1;
            if (size > Integer.MAX_VALUE) {
                throw new IllegalArgumentException(
                    "Counting sort range is too large to index."
                );
            }

            return (int) size;
        }

        private int indexOf(int value) {
            return value - minValue;
        }
    }
}
