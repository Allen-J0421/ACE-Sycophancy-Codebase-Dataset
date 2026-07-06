import java.util.Comparator;

public class HeapSort {

    public static void heapSort(int[] arr) {
        heapSort(arr, 0, arr.length);
    }

    public static void heapSort(int[] arr, int from, int to) {
        if (from < 0 || to > arr.length || from > to)
            throw new IllegalArgumentException(
                "Invalid range [" + from + ", " + to + ") for array of length " + arr.length);
        SortContext.forIntRange(arr, from).sort(to - from);
    }

    public static <T> void heapSort(T[] arr, Comparator<T> cmp) {
        heapSort(arr, 0, arr.length, cmp);
    }

    public static <T> void heapSort(T[] arr, int from, int to, Comparator<T> cmp) {
        if (from < 0 || to > arr.length || from > to)
            throw new IllegalArgumentException(
                "Invalid range [" + from + ", " + to + ") for array of length " + arr.length);
        SortContext.forObjectRange(arr, from, to, cmp).sort();
    }
}
