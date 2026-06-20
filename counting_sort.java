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

        Bounds bounds = findBounds(values);
        int[] cumulativeCounts = buildCumulativeCounts(values, bounds);
        return buildSortedArray(values, cumulativeCounts, bounds);
    }

    private static Bounds findBounds(int[] values) {
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

        return new Bounds(minVal, maxVal);
    }

    private static int[] buildCumulativeCounts(int[] values, Bounds bounds) {
        int[] counts = new int[bounds.size()];
        for (int value : values) {
            counts[bounds.indexOf(value)]++;
        }

        for (int i = 1; i < counts.length; i++) {
            counts[i] += counts[i - 1];
        }

        return counts;
    }

    private static int[] buildSortedArray(int[] values, int[] counts, Bounds bounds) {
        int[] sorted = new int[values.length];
        for (int i = values.length - 1; i >= 0; i--) {
            int value = values[i];
            int countIndex = bounds.indexOf(value);
            sorted[counts[countIndex] - 1] = value;
            counts[countIndex]--;
        }

        return sorted;
    }

    private static final class Bounds {
        private final int min;
        private final int size;

        private Bounds(int min, int max) {
            this.min = min;
            this.size = calculateSize(min, max);
        }

        private int size() {
            return size;
        }

        private int indexOf(int value) {
            return value - min;
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
