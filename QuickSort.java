import java.util.Objects;

public final class QuickSort {
    private static final int INSERTION_SORT_THRESHOLD = 16;

    private QuickSort() {
        // Utility class.
    }

    public static void sort(int[] values) {
        Objects.requireNonNull(values, "values");
        sortRange(values, 0, values.length);
    }

    public static void sort(int[] values, int fromIndex, int toIndexExclusive) {
        Objects.requireNonNull(values, "values");
        sortRange(values, fromIndex, toIndexExclusive);
    }

    private static void sortRange(int[] values, int fromIndex, int toIndexExclusive) {
        Objects.checkFromToIndex(fromIndex, toIndexExclusive, values.length);
        if (toIndexExclusive - fromIndex < 2) {
            return;
        }

        quickSort(values, fromIndex, toIndexExclusive);
    }

    private static void quickSort(int[] values, int left, int rightExclusive) {
        while (rightExclusive - left > INSERTION_SORT_THRESHOLD) {
            int pivotIndex = partition(values, left, rightExclusive);

            if (pivotIndex - left < rightExclusive - (pivotIndex + 1)) {
                quickSort(values, left, pivotIndex);
                left = pivotIndex + 1;
            } else {
                quickSort(values, pivotIndex + 1, rightExclusive);
                rightExclusive = pivotIndex;
            }
        }

        insertionSort(values, left, rightExclusive);
    }

    private static int partition(int[] values, int left, int rightExclusive) {
        int right = rightExclusive - 1;
        int pivotIndex = medianOfThree(values, left, right);
        int pivot = values[pivotIndex];
        swap(values, pivotIndex, right);

        int storeIndex = left;

        for (int index = left; index < right; index++) {
            if (values[index] < pivot) {
                swap(values, storeIndex, index);
                storeIndex++;
            }
        }

        swap(values, storeIndex, right);
        return storeIndex;
    }

    private static int medianOfThree(int[] values, int left, int right) {
        int middle = left + ((right - left) >>> 1);

        if (values[left] > values[middle]) {
            swap(values, left, middle);
        }
        if (values[left] > values[right]) {
            swap(values, left, right);
        }
        if (values[middle] > values[right]) {
            swap(values, middle, right);
        }

        return middle;
    }

    private static void insertionSort(int[] values, int left, int rightExclusive) {
        for (int index = left + 1; index < rightExclusive; index++) {
            int currentValue = values[index];
            int position = index - 1;

            while (position >= left && values[position] > currentValue) {
                values[position + 1] = values[position];
                position--;
            }

            values[position + 1] = currentValue;
        }
    }

    private static void swap(int[] values, int first, int second) {
        int temp = values[first];
        values[first] = values[second];
        values[second] = temp;
    }
}
