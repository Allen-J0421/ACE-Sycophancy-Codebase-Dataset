import java.util.Comparator;
import java.util.Objects;

/**
 * Insertion sort implementation of {@link Sorter}.
 *
 * <p>Builds the sorted array one element at a time: each new element is compared
 * against the already-sorted prefix to its left and shifted into place. Runs in
 * O(n^2) time in the worst case and O(n) on already-sorted input, sorting in
 * place with O(1) extra space. The sort is stable.
 *
 * <p>Stateless and therefore safe to share between threads.
 */
public final class InsertionSorter implements Sorter {

    @Override
    public void sort(int[] arr) {
        Objects.requireNonNull(arr, "arr");
        for (int i = 1; i < arr.length; i++) {
            int key = arr[i];
            int j = i - 1;

            // Shift elements greater than key one position to the right.
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    @Override
    public <T> void sort(T[] arr, Comparator<? super T> comparator) {
        Objects.requireNonNull(arr, "arr");
        Objects.requireNonNull(comparator, "comparator");
        for (int i = 1; i < arr.length; i++) {
            T key = arr[i];
            int j = i - 1;

            // Shift elements that should come after key one position to the right.
            while (j >= 0 && comparator.compare(arr[j], key) > 0) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }
}
