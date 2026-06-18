public class QuickSort implements Sorter {

    private final PivotSelector pivotSelector;

    public QuickSort() {
        this(PivotSelector.LAST_ELEMENT);
    }

    public QuickSort(PivotSelector pivotSelector) {
        this.pivotSelector = pivotSelector;
    }

    @Override
    public void sort(int[] arr) {
        sort(arr, 0, arr.length - 1);
    }

    private void sort(int[] arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            sort(arr, low, pi - 1);
            sort(arr, pi + 1, high);
        }
    }

    private int partition(int[] arr, int low, int high) {
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

    private void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
