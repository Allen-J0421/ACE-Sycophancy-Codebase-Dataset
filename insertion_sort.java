import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

class InsertionSort {

    private InsertionSort() {}

    public static void sort(int[] arr) {
        Objects.requireNonNull(arr, "arr must not be null");
        for (int i = 1; i < arr.length; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    public static int[] sortedCopy(int[] arr) {
        int[] copy = arr.clone();
        sort(copy);
        return copy;
    }

    public static <T> void sort(T[] arr, Comparator<? super T> comparator) {
        Objects.requireNonNull(arr, "arr must not be null");
        Objects.requireNonNull(comparator, "comparator must not be null");
        for (int i = 1; i < arr.length; i++) {
            T key = arr[i];
            int j = i - 1;
            while (j >= 0 && comparator.compare(arr[j], key) > 0) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    public static <T extends Comparable<? super T>> void sort(T[] arr) {
        sort(arr, Comparator.naturalOrder());
    }

    public static void main(String[] args) {
        int[] arr = {12, 11, 13, 5, 6};
        System.out.println(Arrays.toString(sortedCopy(arr)));
    }
}
