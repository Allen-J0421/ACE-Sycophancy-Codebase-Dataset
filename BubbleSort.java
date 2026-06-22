import java.util.Arrays;

public final class BubbleSort {

    private BubbleSort() {
        // Utility class.
    }

    public static void sortInPlace(int[] values) {
        validate(values);

        if (values.length < 2) {
            return;
        }

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
        for (int end = values.length - 1; end > 0; end--) {
            boolean swapped = false;

            for (int index = 0; index < end; index++) {
                if (values[index] > values[index + 1]) {
                    swap(values, index, index + 1);
                    swapped = true;
                }
            }

            if (!swapped) {
                return;
            }
        }
    }

    private static void swap(int[] values, int leftIndex, int rightIndex) {
        int temp = values[leftIndex];
        values[leftIndex] = values[rightIndex];
        values[rightIndex] = temp;
    }
}
