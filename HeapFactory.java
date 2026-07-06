import java.util.Comparator;

interface HeapFactory {
    AbstractHeap forIntRange(int[] arr, int from);
    <T> Heap<T> forObjectRange(T[] arr, int from, int to, Comparator<T> cmp);
}
