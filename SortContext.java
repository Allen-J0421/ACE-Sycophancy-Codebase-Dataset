import java.util.Comparator;

final class SortContext implements HeapFactory {

    @Override
    public HeapSorter<Integer> forIntArray(int[] arr) {
        return new RangeHeap(arr);
    }

    @Override
    public <T> HeapSorter<T> forObjectArray(T[] arr, Comparator<T> cmp) {
        return new ObjectRangeHeap<>(arr, cmp);
    }
}
