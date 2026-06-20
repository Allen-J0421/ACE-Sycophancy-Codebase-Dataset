import java.util.Objects;

/**
 * Stable counting sort for integer arrays.
 */
public final class CountingSort {

    private CountingSort() {
    }

    public static int[] countSort(int[] values) {
        return sortedCopy(values);
    }

    public static int[] sort(int[] values) {
        return sortedCopy(values);
    }

    public static int[] sortedCopy(int[] values) {
        Objects.requireNonNull(values, "values");

        if (values.length == 0) {
            return new int[0];
        }

        Range range = findRange(values);
        int[] prefixCounts = buildPrefixCounts(values, range);
        return buildSortedArray(values, prefixCounts, range);
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

    private static int[] buildSortedArray(int[] values, int[] prefixCounts, Range range) {
        int[] sortedValues = new int[values.length];

        for (int i = values.length - 1; i >= 0; i--) {
            int value = values[i];
            int countIndex = range.indexOf(value);
            sortedValues[prefixCounts[countIndex] - 1] = value;
            prefixCounts[countIndex]--;
        }

        return sortedValues;
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
