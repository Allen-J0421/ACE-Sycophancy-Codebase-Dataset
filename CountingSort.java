import java.util.Arrays;
import java.util.Objects;

public final class CountingSort {

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

        Range range = findRange(values);
        int[] counts = buildCounts(values, range);
        accumulateCounts(counts);
        return buildSortedArray(values, counts, range.minValue());
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

    private static int[] buildCounts(int[] values, Range range) {
        int[] counts = new int[range.size()];
        int offset = range.minValue();

        for (int value : values) {
            counts[value - offset]++;
        }

        return counts;
    }

    private static void accumulateCounts(int[] counts) {
        for (int i = 1; i < counts.length; i++) {
            counts[i] += counts[i - 1];
        }
    }

    private static int[] buildSortedArray(int[] values, int[] counts, int minValue) {
        int[] sortedValues = new int[values.length];

        for (int i = values.length - 1; i >= 0; i--) {
            int value = values[i];
            int countIndex = value - minValue;
            sortedValues[counts[countIndex] - 1] = value;
            counts[countIndex]--;
        }

        return sortedValues;
    }

    private record Range(int minValue, int maxValue) {
        private int size() {
            return maxValue - minValue + 1;
        }
    }

    public static void main(String[] args) {
        int[] values = {2, 5, 3, 0, 2, 3, 0, 3, -4, -1};
        int[] sortedValues = sort(values);
        System.out.println(Arrays.toString(sortedValues));
    }
}
