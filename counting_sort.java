import java.util.Arrays;
import java.util.Objects;

final class CountingSort {

    private CountingSort() {
    }

    public static int[] countSort(int[] values) {
        return sort(values);
    }

    public static int[] sort(int[] values) {
        Objects.requireNonNull(values, "values");
        if (values.length == 0) {
            return new int[0];
        }

        ValueRange range = ValueRange.from(values);
        int[] counts = buildCounts(values, range);
        return buildSortedArray(counts, range, values.length);
    }

    private static int[] buildCounts(int[] values, ValueRange range) {
        int[] counts = new int[range.size()];
        for (int value : values) {
            counts[range.indexOf(value)]++;
        }
        return counts;
    }

    private static int[] buildSortedArray(int[] counts, ValueRange range, int length) {
        int[] sorted = new int[length];
        int sortedIndex = 0;
        for (int countIndex = 0; countIndex < counts.length; countIndex++) {
            int value = range.valueAt(countIndex);
            for (int occurrence = 0; occurrence < counts[countIndex]; occurrence++) {
                sorted[sortedIndex] = value;
                sortedIndex++;
            }
        }

        return sorted;
    }

    private static final class ValueRange {
        private final int min;
        private final int size;

        private ValueRange(int min, int max) {
            this.min = min;
            this.size = calculateSize(min, max);
        }

        private static ValueRange from(int[] values) {
            int minVal = values[0];
            int maxVal = values[0];
            for (int i = 1; i < values.length; i++) {
                if (values[i] < minVal) {
                    minVal = values[i];
                }
                if (values[i] > maxVal) {
                    maxVal = values[i];
                }
            }

            return new ValueRange(minVal, maxVal);
        }

        private int size() {
            return size;
        }

        private int indexOf(int value) {
            return value - min;
        }

        private int valueAt(int index) {
            return min + index;
        }

        private static int calculateSize(int min, int max) {
            long size = (long) max - min + 1;
            if (size > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("Input range is too large to sort with counting sort.");
            }
            return (int) size;
        }
    }

    public static void main(String[] args) {
        int[] values = {2, 5, 3, 0, 2, 3, 0, 3};
        int[] sorted = sort(values);
        System.out.println(Arrays.toString(sorted));
    }
}
