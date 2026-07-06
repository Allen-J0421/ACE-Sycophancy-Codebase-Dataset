import java.util.Comparator;

final class SortContext implements HeapFactory {

    @Override
    public AbstractHeap forIntRange(int[] arr, int from) {
        return new RangeHeap(arr, from);
    }

    @Override
    public <T> Heap<T> forObjectRange(T[] arr, int from, int to, Comparator<T> cmp) {
        return new ObjectRangeHeap<>(arr, from, to, cmp);
    }
}
