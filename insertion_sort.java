import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

class InsertionSort {

    private InsertionSort() {}

    @FunctionalInterface
    private interface IntBiPredicate {
        boolean test(int a, int b);
    }

    @FunctionalInterface
    private interface IntBiConsumer {
        void accept(int a, int b);
    }

    private static void sortCore(int fromIndex, int toIndex,
            IntBiPredicate isOutOfOrder, IntBiConsumer swap) {
        for (int i = fromIndex + 1; i < toIndex; i++) {
            int j = i;
            while (j > fromIndex && isOutOfOrder.test(j - 1, j)) {
                swap.accept(j - 1, j);
                j--;
            }
        }
    }

    // --- int[] overloads ---

    public static void sort(int[] arr) {
        Objects.requireNonNull(arr, "arr must not be null");
        sort(arr, 0, arr.length);
    }

    public static void sort(int[] arr, int fromIndex, int toIndex) {
        Objects.requireNonNull(arr, "arr must not be null");
        Objects.checkFromToIndex(fromIndex, toIndex, arr.length);
        sortCore(fromIndex, toIndex,
            (i, j) -> arr[i] > arr[j],
            (i, j) -> { int t = arr[i]; arr[i] = arr[j]; arr[j] = t; });
    }

    public static int[] sortedCopy(int[] arr) {
        Objects.requireNonNull(arr, "arr must not be null");
        int[] copy = arr.clone();
        sort(copy, 0, copy.length);
        return copy;
    }

    // --- generic T[] overloads ---

    public static <T extends Comparable<? super T>> void sort(T[] arr) {
        sort(arr, Comparator.naturalOrder());
    }

    public static <T> void sort(T[] arr, Comparator<? super T> comparator) {
        Objects.requireNonNull(arr, "arr must not be null");
        sort(arr, 0, arr.length, comparator);
    }

    public static <T extends Comparable<? super T>> void sort(T[] arr, int fromIndex, int toIndex) {
        sort(arr, fromIndex, toIndex, Comparator.naturalOrder());
    }

    public static <T> void sort(T[] arr, int fromIndex, int toIndex, Comparator<? super T> comparator) {
        Objects.requireNonNull(arr, "arr must not be null");
        Objects.requireNonNull(comparator, "comparator must not be null");
        Objects.checkFromToIndex(fromIndex, toIndex, arr.length);
        sortCore(fromIndex, toIndex,
            (i, j) -> comparator.compare(arr[i], arr[j]) > 0,
            (i, j) -> { T t = arr[i]; arr[i] = arr[j]; arr[j] = t; });
    }

    public static <T extends Comparable<? super T>> T[] sortedCopy(T[] arr) {
        Objects.requireNonNull(arr, "arr must not be null");
        T[] copy = arr.clone();
        sort(copy, 0, copy.length, Comparator.naturalOrder());
        return copy;
    }

    public static <T> T[] sortedCopy(T[] arr, Comparator<? super T> comparator) {
        Objects.requireNonNull(arr, "arr must not be null");
        Objects.requireNonNull(comparator, "comparator must not be null");
        T[] copy = arr.clone();
        sort(copy, 0, copy.length, comparator);
        return copy;
    }

    public static void main(String[] args) {
        int[] arr = {12, 11, 13, 5, 6};
        System.out.println(Arrays.toString(sortedCopy(arr)));
    }
}
