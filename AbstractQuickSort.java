abstract class AbstractQuickSort implements Sorter {

    protected final PivotSelector pivotSelector;

    protected AbstractQuickSort(PivotSelector pivotSelector) {
        this.pivotSelector = pivotSelector;
    }

    protected int partition(int[] arr, int low, int high) {
        int pivotIndex = pivotSelector.selectPivot(arr, low, high);
        swap(arr, pivotIndex, high);

        int pivot = arr[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (arr[j] < pivot) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
    }

    protected void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
