import java.util.Comparator;

interface HeapFactory {
    HeapSorter<Integer> forIntArray(int[] arr);
    <T> HeapSorter<T> forObjectArray(T[] arr, Comparator<T> cmp);
}
