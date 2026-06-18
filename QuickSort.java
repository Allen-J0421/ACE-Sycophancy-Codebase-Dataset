import java.util.Arrays;
import java.util.Objects;

public final class QuickSort {

    private static final int INSERTION_SORT_THRESHOLD = 12;

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

        quickSort(values, fromIndex, toIndex);
    }

    public static int[] sortedCopy(int[] values) {
        Objects.requireNonNull(values, "values");

        int[] copy = Arrays.copyOf(values, values.length);
        sort(copy);
        return copy;
    }

    private static void quickSort(int[] values, int fromIndex, int toIndex) {
        while (toIndex - fromIndex > INSERTION_SORT_THRESHOLD) {
            int pivotIndex = partition(values, fromIndex, toIndex);

            if (pivotIndex - fromIndex < toIndex - (pivotIndex + 1)) {
                quickSort(values, fromIndex, pivotIndex);
                fromIndex = pivotIndex + 1;
            } else {
                quickSort(values, pivotIndex + 1, toIndex);
                toIndex = pivotIndex;
            }
        }

        insertionSort(values, fromIndex, toIndex);
    }

    private static int partition(int[] values, int fromIndex, int toIndex) {
        int pivotIndex = toIndex - 1;
        int pivot = values[pivotIndex];
        int smallerElementBoundary = fromIndex;

        for (int currentIndex = fromIndex; currentIndex < pivotIndex; currentIndex++) {
            if (values[currentIndex] < pivot) {
                swap(values, smallerElementBoundary, currentIndex);
                smallerElementBoundary++;
            }
        }

        swap(values, smallerElementBoundary, pivotIndex);
        return smallerElementBoundary;
    }

    private static void insertionSort(int[] values, int fromIndex, int toIndex) {
        for (int index = fromIndex + 1; index < toIndex; index++) {
            int currentValue = values[index];
            int insertionIndex = index - 1;

            while (insertionIndex >= fromIndex && values[insertionIndex] > currentValue) {
                values[insertionIndex + 1] = values[insertionIndex];
                insertionIndex--;
            }

            values[insertionIndex + 1] = currentValue;
        }
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
