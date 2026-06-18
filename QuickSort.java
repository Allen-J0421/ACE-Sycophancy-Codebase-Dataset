import java.util.Arrays;
import java.util.Objects;

public final class QuickSort {

    private QuickSort() {
    }

    public static void sort(int[] values) {
        Objects.requireNonNull(values, "values");
        sort(values, 0, values.length);
    }

    public static void sort(int[] values, int fromIndex, int toIndex) {
        Objects.requireNonNull(values, "values");
        validateRange(values.length, fromIndex, toIndex);

        if (toIndex - fromIndex < 2) {
            return;
        }

        quickSort(values, fromIndex, toIndex - 1);
    }

    public static int[] sortedCopy(int[] values) {
        Objects.requireNonNull(values, "values");

        int[] copy = Arrays.copyOf(values, values.length);
        sort(copy);
        return copy;
    }

    private static void quickSort(int[] values, int low, int high) {
        while (low < high) {
            int pivotIndex = partition(values, low, high);

            if (pivotIndex - low < high - pivotIndex) {
                quickSort(values, low, pivotIndex - 1);
                low = pivotIndex + 1;
            } else {
                quickSort(values, pivotIndex + 1, high);
                high = pivotIndex - 1;
            }
        }
    }

    private static int partition(int[] values, int low, int high) {
        int pivot = values[high];
        int smallerElementBoundary = low;

        for (int currentIndex = low; currentIndex < high; currentIndex++) {
            if (values[currentIndex] < pivot) {
                swap(values, smallerElementBoundary, currentIndex);
                smallerElementBoundary++;
            }
        }

        swap(values, smallerElementBoundary, high);
        return smallerElementBoundary;
    }

    private static void swap(int[] values, int leftIndex, int rightIndex) {
        if (leftIndex == rightIndex) {
            return;
        }

        int temporaryValue = values[leftIndex];
        values[leftIndex] = values[rightIndex];
        values[rightIndex] = temporaryValue;
    }

    private static void validateRange(int length, int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex > length) {
            throw new IndexOutOfBoundsException(
                    "Range [" + fromIndex + ", " + toIndex + ") out of bounds for length " + length);
        }

        if (fromIndex > toIndex) {
            throw new IllegalArgumentException(
                    "fromIndex (" + fromIndex + ") must be <= toIndex (" + toIndex + ")");
        }
    }
}
