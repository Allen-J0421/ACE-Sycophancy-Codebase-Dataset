public class QuickSort extends AbstractQuickSort {

    public QuickSort() {
        this(PivotSelector.LAST_ELEMENT);
    }

    public QuickSort(PivotSelector pivotSelector) {
        super(pivotSelector);
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
}
