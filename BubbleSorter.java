import java.util.Comparator;
import java.util.Objects;

/**
 * Bubble sort implementation of {@link Sorter}.
 *
 * <p>Repeatedly steps through the array, swapping adjacent elements that are out
 * of order, so that the largest remaining element "bubbles" to the end of each
 * pass. Runs in O(n^2) time in the worst case; the {@code swapped} early-exit
 * makes it O(n) on already-sorted input. Sorts in place with O(1) extra space
 * and is stable (only strictly out-of-order pairs are swapped).
 *
 * <p>Stateless and therefore safe to share between threads.
 */
public final class BubbleSorter implements Sorter {

    @Override
    public void sort(int[] arr) {
        Objects.requireNonNull(arr, "arr");
        // After pass k, the last k elements are in their final position.
        for (int end = arr.length; end > 1; end--) {
            boolean swapped = false;
            for (int i = 1; i < end; i++) {
                if (arr[i - 1] > arr[i]) {
                    int tmp = arr[i - 1];
                    arr[i - 1] = arr[i];
                    arr[i] = tmp;
                    swapped = true;
                }
            }
            if (!swapped) {
                break; // No swaps in a full pass => already sorted.
            }
        }
    }

    @Override
    public <T> void sort(T[] arr, Comparator<? super T> comparator) {
        Objects.requireNonNull(arr, "arr");
        Objects.requireNonNull(comparator, "comparator");
        for (int end = arr.length; end > 1; end--) {
            boolean swapped = false;
            for (int i = 1; i < end; i++) {
                if (comparator.compare(arr[i - 1], arr[i]) > 0) {
                    T tmp = arr[i - 1];
                    arr[i - 1] = arr[i];
                    arr[i] = tmp;
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
    }
}
