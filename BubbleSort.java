import java.util.Arrays;

public final class BubbleSort {

    private BubbleSort() {
        // Utility class.
    }

    public static void sortInPlace(int[] values) {
        validate(values);
        sortValuesInPlace(values);
    }

    public static int[] sortedCopy(int[] values) {
        validate(values);

        int[] copy = Arrays.copyOf(values, values.length);
        sortValuesInPlace(copy);
        return copy;
    }

    private static void validate(int[] values) {
        if (values == null) {
            throw new IllegalArgumentException("values must not be null");
        }
    }

    private static void sortValuesInPlace(int[] values) {
        int end = values.length - 1;
        while (end > 0) {
            int lastSwapIndex = bubblePass(values, end);
            if (lastSwapIndex == 0) {
                return;
            }
            end = lastSwapIndex - 1;
        }
    }

    private static int bubblePass(int[] values, int endExclusive) {
        int lastSwapIndex = 0;

        for (int index = 0; index < endExclusive; index++) {
            if (isOutOfOrder(values, index)) {
                swap(values, index, index + 1);
                lastSwapIndex = index + 1;
            }
        }

        return lastSwapIndex;
    }

    private static boolean isOutOfOrder(int[] values, int index) {
        return values[index] > values[index + 1];
    }

    private static void swap(int[] values, int leftIndex, int rightIndex) {
        int temp = values[leftIndex];
        values[leftIndex] = values[rightIndex];
        values[rightIndex] = temp;
    }
}
