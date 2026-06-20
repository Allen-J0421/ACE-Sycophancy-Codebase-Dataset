import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

class InsertionSort {

    private InsertionSort() {}

    private static final String NULL_ARRAY_MSG = "arr must not be null";
    private static final String NULL_LIST_MSG = "list must not be null";
    private static final String NULL_COMPARATOR_MSG = "comparator must not be null";

    @FunctionalInterface
    private interface IndexPredicate {
        boolean test(int a, int b);
    }

    @FunctionalInterface
    private interface IndexSwapper {
        void swap(int a, int b);
    }

    private static void sortCore(int fromIndex, int toIndex,
            IndexPredicate isOutOfOrder, IndexSwapper swapper) {
        for (int i = fromIndex + 1; i < toIndex; i++) {
            int j = i;
            while (j > fromIndex && isOutOfOrder.test(j - 1, j)) {
                swapper.swap(j - 1, j);
                j--;
            }
        }
    }

    public static void sort(int[] arr) {
        Objects.requireNonNull(arr, NULL_ARRAY_MSG);
        sort(arr, 0, arr.length);
    }

    public static void sort(int[] arr, int fromIndex, int toIndex) {
        Objects.requireNonNull(arr, NULL_ARRAY_MSG);
        Objects.checkFromToIndex(fromIndex, toIndex, arr.length);
        sortCore(fromIndex, toIndex,
            (i, j) -> arr[i] > arr[j],
            (i, j) -> { int t = arr[i]; arr[i] = arr[j]; arr[j] = t; });
    }

    public static int[] sortedCopy(int[] arr) {
        Objects.requireNonNull(arr, NULL_ARRAY_MSG);
        int[] copy = arr.clone();
        sort(copy);
        return copy;
    }

    public static <T extends Comparable<? super T>> void sort(T[] arr) {
        sort(arr, Comparator.naturalOrder());
    }

    public static <T> void sort(T[] arr, Comparator<? super T> comparator) {
        Objects.requireNonNull(arr, NULL_ARRAY_MSG);
        sort(arr, 0, arr.length, comparator);
    }

    public static <T extends Comparable<? super T>> void sort(T[] arr, int fromIndex, int toIndex) {
        sort(arr, fromIndex, toIndex, Comparator.naturalOrder());
    }

    public static <T> void sort(T[] arr, int fromIndex, int toIndex, Comparator<? super T> comparator) {
        Objects.requireNonNull(arr, NULL_ARRAY_MSG);
        Objects.requireNonNull(comparator, NULL_COMPARATOR_MSG);
        Objects.checkFromToIndex(fromIndex, toIndex, arr.length);
        sortCore(fromIndex, toIndex,
            (i, j) -> comparator.compare(arr[i], arr[j]) > 0,
            (i, j) -> { T t = arr[i]; arr[i] = arr[j]; arr[j] = t; });
    }

    public static <T extends Comparable<? super T>> T[] sortedCopy(T[] arr) {
        Objects.requireNonNull(arr, NULL_ARRAY_MSG);
        T[] copy = arr.clone();
        sort(copy);
        return copy;
    }

    public static <T> T[] sortedCopy(T[] arr, Comparator<? super T> comparator) {
        Objects.requireNonNull(arr, NULL_ARRAY_MSG);
        Objects.requireNonNull(comparator, NULL_COMPARATOR_MSG);
        T[] copy = arr.clone();
        sort(copy, comparator);
        return copy;
    }

    public static <T extends Comparable<? super T>> void sort(List<T> list) {
        sort(list, Comparator.naturalOrder());
    }

    public static <T> void sort(List<T> list, Comparator<? super T> comparator) {
        Objects.requireNonNull(list, NULL_LIST_MSG);
        Objects.requireNonNull(comparator, NULL_COMPARATOR_MSG);
        sortCore(0, list.size(),
            (i, j) -> comparator.compare(list.get(i), list.get(j)) > 0,
            (i, j) -> Collections.swap(list, i, j));
    }

    public static <T extends Comparable<? super T>> List<T> sortedCopy(List<T> list) {
        return sortedCopy(list, Comparator.naturalOrder());
    }

    public static <T> List<T> sortedCopy(List<T> list, Comparator<? super T> comparator) {
        Objects.requireNonNull(list, NULL_LIST_MSG);
        Objects.requireNonNull(comparator, NULL_COMPARATOR_MSG);
        List<T> copy = new ArrayList<>(list);
        sort(copy, comparator);
        return copy;
    }

    public static void main(String[] args) {
        int[] arr = {12, 11, 13, 5, 6};
        System.out.println(Arrays.toString(sortedCopy(arr)));
    }
}
