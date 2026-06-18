import java.util.Arrays;

final class QuickSort {

    private QuickSort() {
    }

    static void sort(int[] values) {
        if (values == null || values.length < 2) {
            return;
        }

        quickSort(values, 0, values.length - 1);
    }

    private static void quickSort(int[] values, int low, int high) {
        if (low >= high) {
            return;
        }

        int pivotIndex = partition(values, low, high);
        quickSort(values, low, pivotIndex - 1);
        quickSort(values, pivotIndex + 1, high);
    }

    private static int partition(int[] values, int low, int high) {
        int pivot = values[high];
        int smallerElementBoundary = low - 1;

        for (int currentIndex = low; currentIndex < high; currentIndex++) {
            if (values[currentIndex] < pivot) {
                smallerElementBoundary++;
                swap(values, smallerElementBoundary, currentIndex);
            }
        }

        int pivotIndex = smallerElementBoundary + 1;
        swap(values, pivotIndex, high);
        return pivotIndex;
    }

    private static void swap(int[] values, int leftIndex, int rightIndex) {
        int temporaryValue = values[leftIndex];
        values[leftIndex] = values[rightIndex];
        values[rightIndex] = temporaryValue;
    }

    public static void main(String[] args) {
        int[] values = {10, 7, 8, 9, 1, 5};
        sort(values);
        System.out.println(Arrays.toString(values));
    }
}
