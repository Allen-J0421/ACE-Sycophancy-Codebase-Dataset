import java.util.Comparator;

class ObjectRangeHeap<T> extends AbstractHeap implements Heap<T> {
    private final T[] arr;
    private final int from;
    private final int size;
    private final Comparator<T> cmp;

    ObjectRangeHeap(T[] arr, int from, int to, Comparator<T> cmp) {
        this.arr = arr;
        this.from = from;
        this.size = to - from;
        this.cmp = cmp;
    }

    @Override
    public void sort() {
        sort(size);
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
