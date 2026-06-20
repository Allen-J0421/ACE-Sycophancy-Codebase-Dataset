import java.util.Arrays;
import java.util.Objects;

public final class CountingSort {

    private CountingSort() {
    }

    public static int[] countSort(int[] values) {
        Objects.requireNonNull(values, "values");

        if (values.length == 0) {
            return new int[0];
        }

        int maxValue = findMaxValue(values);
        int[] counts = buildCounts(values, maxValue);
        accumulateCounts(counts);
        return buildSortedArray(values, counts);
    }

    private static int findMaxValue(int[] values) {
        int maxValue = validateNonNegative(values[0]);

        for (int i = 1; i < values.length; i++) {
            int value = validateNonNegative(values[i]);
            if (value > maxValue) {
                maxValue = value;
            }
        }

        return maxValue;
    }

    private static int[] buildCounts(int[] values, int maxValue) {
        int[] counts = new int[maxValue + 1];

        for (int value : values) {
            counts[value]++;
        }

        return counts;
    }

    private static void accumulateCounts(int[] counts) {
        for (int i = 1; i < counts.length; i++) {
            counts[i] += counts[i - 1];
        }
    }

    private static int[] buildSortedArray(int[] values, int[] counts) {
        int[] sortedValues = new int[values.length];

        for (int i = values.length - 1; i >= 0; i--) {
            int value = values[i];
            sortedValues[counts[value] - 1] = value;
            counts[value]--;
        }

        return sortedValues;
    }

    private static int validateNonNegative(int value) {
        if (value < 0) {
            throw new IllegalArgumentException(
                "Counting sort supports only non-negative integers."
            );
        }

        return value;
    }

    public static void main(String[] args) {
        int[] arr = {2, 5, 3, 0, 2, 3, 0, 3};
        int[] ans = countSort(arr);
        System.out.println(Arrays.toString(ans));
    }
}
