import java.util.Comparator;

public class HeapSort {

    private static final HeapFactory FACTORY = new SortContext();

    public static void heapSort(int[] arr) {
        heapSort(arr, 0, arr.length);
    }

    public static void heapSort(int[] arr, int from, int to) {
        if (from < 0 || to > arr.length || from > to)
            throw new IllegalArgumentException(
                "Invalid range [" + from + ", " + to + ") for array of length " + arr.length);
        FACTORY.forIntArray(arr).sort(from, to);
    }

    public static <T> void heapSort(T[] arr, Comparator<T> cmp) {
        heapSort(arr, 0, arr.length, cmp);
    }

    public static <T> void heapSort(T[] arr, int from, int to, Comparator<T> cmp) {
        if (from < 0 || to > arr.length || from > to)
            throw new IllegalArgumentException(
                "Invalid range [" + from + ", " + to + ") for array of length " + arr.length);
        FACTORY.forObjectArray(arr, cmp).sort(from, to);
    }
}
