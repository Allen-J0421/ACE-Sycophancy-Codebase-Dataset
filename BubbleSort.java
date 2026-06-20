import java.util.Objects;

public final class BubbleSort {
    private BubbleSort() {
        // Utility class.
    }

    public static void sort(int[] values) {
        Objects.requireNonNull(values, "values");

        if (hasFewerThanTwoValues(values)) {
            return;
        }

        for (int lastUnsortedIndex = values.length - 1; lastUnsortedIndex > 0; lastUnsortedIndex--) {
            if (!bubbleLargestValueToEnd(values, lastUnsortedIndex)) {
                return;
            }
        }
    }

    public static int[] sortedCopy(int[] values) {
        Objects.requireNonNull(values, "values");

        int[] sortedValues = values.clone();
        sort(sortedValues);
        return sortedValues;
    }

    private static boolean hasFewerThanTwoValues(int[] values) {
        return values.length < 2;
    }

    private static boolean bubbleLargestValueToEnd(int[] values, int lastUnsortedIndex) {
        boolean swapped = false;

        for (int currentIndex = 0; currentIndex < lastUnsortedIndex; currentIndex++) {
            if (values[currentIndex] > values[currentIndex + 1]) {
                swap(values, currentIndex, currentIndex + 1);
                swapped = true;
            }
        }

        return swapped;
    }

    private static void swap(int[] values, int leftIndex, int rightIndex) {
        int temporary = values[leftIndex];
        values[leftIndex] = values[rightIndex];
        values[rightIndex] = temporary;
    }
}
