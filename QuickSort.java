import java.util.Arrays;
import java.util.Objects;

public final class QuickSort {
    private static final int INSERTION_SORT_THRESHOLD = 16;

    private QuickSort() {
        // Utility class.
    }

    public static void sort(int[] values) {
        Objects.requireNonNull(values, "values");
        if (values.length < 2) {
            return;
        }

        quickSort(values, 0, values.length - 1);
    }

    private static void quickSort(int[] values, int left, int right) {
        while (right - left > INSERTION_SORT_THRESHOLD) {
            int pivotIndex = partition(values, left, right);

            if (pivotIndex - left < right - pivotIndex) {
                quickSort(values, left, pivotIndex - 1);
                left = pivotIndex + 1;
            } else {
                quickSort(values, pivotIndex + 1, right);
                right = pivotIndex - 1;
            }
        }

        insertionSort(values, left, right);
    }

    private static int partition(int[] values, int left, int right) {
        int pivotIndex = medianOfThree(values, left, right);
        int pivot = values[pivotIndex];
        swap(values, pivotIndex, right);

        int storeIndex = left - 1;

        for (int index = left; index < right; index++) {
            if (values[index] < pivot) {
                storeIndex++;
                swap(values, storeIndex, index);
            }
        }

        swap(values, storeIndex + 1, right);
        return storeIndex + 1;
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

    private static void insertionSort(int[] values, int left, int right) {
        for (int index = left + 1; index <= right; index++) {
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

    public static void main(String[] args) {
        int[] values = {10, 7, 8, 9, 1, 5};
        sort(values);
        System.out.println(Arrays.toString(values));
    }
}
