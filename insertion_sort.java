import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

class InsertionSort {

    private InsertionSort() {}

    // --- int[] overloads ---

    public static void sort(int[] arr) {
        Objects.requireNonNull(arr, "arr must not be null");
        sort(arr, 0, arr.length);
    }

    public static void sort(int[] arr, int fromIndex, int toIndex) {
        Objects.requireNonNull(arr, "arr must not be null");
        Objects.checkFromToIndex(fromIndex, toIndex, arr.length);
        for (int i = fromIndex + 1; i < toIndex; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= fromIndex && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
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
        for (int i = fromIndex + 1; i < toIndex; i++) {
            T key = arr[i];
            int j = i - 1;
            while (j >= fromIndex && comparator.compare(arr[j], key) > 0) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
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
