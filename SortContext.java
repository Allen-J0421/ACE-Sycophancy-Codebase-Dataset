import java.util.Comparator;

final class SortContext implements HeapFactory {

    static final SortContext INSTANCE = new SortContext();

    private SortContext() {}

    @Override
    public HeapSorter<Integer> forIntArray(int[] arr) {
        return (from, to) -> new AbstractHeap() {
            @Override
            protected boolean isGreater(int i, int j) {
                return arr[from + i] > arr[from + j];
            }

            @Override
            protected void swap(int i, int j) {
                int tmp = arr[from + i];
                arr[from + i] = arr[from + j];
                arr[from + j] = tmp;
            }
        }.sort(to - from);
    }

    @Override
    public <T> HeapSorter<T> forObjectArray(T[] arr, Comparator<T> cmp) {
        return (from, to) -> new AbstractHeap() {
            @Override
            protected boolean isGreater(int i, int j) {
                return cmp.compare(arr[from + i], arr[from + j]) > 0;
            }

            @Override
            protected void swap(int i, int j) {
                T tmp = arr[from + i];
                arr[from + i] = arr[from + j];
                arr[from + j] = tmp;
            }
        }.sort(to - from);
    }
}
