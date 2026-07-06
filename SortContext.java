import java.util.Comparator;

final class SortContext implements HeapFactory {

    static final SortContext INSTANCE = new SortContext();

    private SortContext() {}

    @Override
    public HeapSorter<Integer> forIntArray(int[] arr) {
        return (from, to) -> AbstractHeap.sort(to - from,
            (i, j) -> arr[from + i] > arr[from + j],
            (i, j) -> { int tmp = arr[from + i]; arr[from + i] = arr[from + j]; arr[from + j] = tmp; });
    }

    @Override
    public <T> HeapSorter<T> forObjectArray(T[] arr, Comparator<T> cmp) {
        return (from, to) -> AbstractHeap.sort(to - from,
            (i, j) -> cmp.compare(arr[from + i], arr[from + j]) > 0,
            (i, j) -> { T tmp = arr[from + i]; arr[from + i] = arr[from + j]; arr[from + j] = tmp; });
    }
}
