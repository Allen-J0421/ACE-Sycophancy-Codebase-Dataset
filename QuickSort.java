public final class QuickSort {

    private QuickSort() {
    }

    public static void sort(int[] values) {
        if (values == null || values.length < 2) {
            return;
        }

        quickSort(values, 0, values.length - 1);
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
}
