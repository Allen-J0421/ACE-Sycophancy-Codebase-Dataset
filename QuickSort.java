import java.util.Arrays;
import java.util.Objects;

public final class QuickSort {

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
        if (left >= right) {
            return;
        }

        int pivotIndex = partition(values, left, right);
        quickSort(values, left, pivotIndex - 1);
        quickSort(values, pivotIndex + 1, right);
    }

    private static int partition(int[] values, int left, int right) {
        int pivot = values[right];
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
