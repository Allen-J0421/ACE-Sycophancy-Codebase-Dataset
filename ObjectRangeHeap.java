import java.util.Comparator;

class ObjectRangeHeap<T> extends AbstractHeap implements HeapSorter<T> {
    private final T[] arr;
    private int from;
    private final Comparator<T> cmp;

    ObjectRangeHeap(T[] arr, Comparator<T> cmp) {
        this.arr = arr;
        this.cmp = cmp;
    }

    @Override
    public void sort(int from, int to) {
        this.from = from;
        sort(to - from);
    }

    @Override
    protected boolean isGreater(int i, int j) {
        return cmp.compare(arr[from + i], arr[from + j]) > 0;
    }

    @Override
    protected void swap(int i, int j) {
        T temp = arr[from + i];
        arr[from + i] = arr[from + j];
        arr[from + j] = temp;
    }
}
