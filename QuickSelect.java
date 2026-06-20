import java.util.Random;

/**
 * Utility for finding order statistics (e.g. the k-th smallest element) of an
 * {@code int} array using the Quickselect algorithm.
 *
 * <p>Quickselect runs in O(n) time on average. A randomized pivot is used so
 * that no specific input (such as already-sorted data) triggers the O(n&sup2;)
 * worst case with high probability. The partitioning loop is iterative, so the
 * algorithm uses O(1) auxiliary space and cannot overflow the call stack.
 *
 * <p>This is a stateless utility class and cannot be instantiated.
 */
public final class QuickSelect {

    private static final Random RANDOM = new Random();

    private QuickSelect() {
        // Utility class: prevent instantiation.
    }

    /**
     * Returns the k-th smallest element of {@code array}.
     *
     * <p>{@code k} is one-based: {@code k = 1} returns the minimum and
     * {@code k = array.length} returns the maximum. The input array is not
     * modified; the algorithm operates on a defensive copy.
     *
     * @param array the values to search; must be non-null and non-empty
     * @param k     the one-based rank of the desired element
     * @return the k-th smallest value in {@code array}
     * @throws NullPointerException     if {@code array} is null
     * @throws IllegalArgumentException if {@code array} is empty or {@code k}
     *                                  is outside the range {@code [1, array.length]}
     */
    public static int kthSmallest(int[] array, int k) {
        if (array == null) {
            throw new NullPointerException("array must not be null");
        }
        if (array.length == 0) {
            throw new IllegalArgumentException("array must not be empty");
        }
        if (k < 1 || k > array.length) {
            throw new IllegalArgumentException(
                "k must be in [1, " + array.length + "] but was " + k);
        }

        // Operate on a copy so the caller's array is left untouched.
        int[] working = array.clone();
        return select(working, k - 1);
    }

    /**
     * Iteratively narrows the search window {@code [low, high]} until the
     * element at index {@code targetIndex} is in its final sorted position.
     */
    private static int select(int[] arr, int targetIndex) {
        int low = 0;
        int high = arr.length - 1;

        while (low < high) {
            int pivotIndex = partition(arr, low, high);
            if (pivotIndex == targetIndex) {
                break;
            } else if (pivotIndex < targetIndex) {
                low = pivotIndex + 1;
            } else {
                high = pivotIndex - 1;
            }
        }
        return arr[targetIndex];
    }

    /**
     * Lomuto partition around a randomly chosen pivot within {@code [low, high]}.
     * After the call every element left of the returned index is strictly less
     * than the pivot and every element to its right is greater than or equal.
     *
     * @return the final resting index of the pivot value
     */
    private static int partition(int[] arr, int low, int high) {
        // Move a random pivot to the end to avoid worst-case behaviour.
        int pivotIndex = low + RANDOM.nextInt(high - low + 1);
        swap(arr, pivotIndex, high);

        int pivot = arr[high];
        int store = low;
        for (int i = low; i < high; i++) {
            if (arr[i] < pivot) {
                swap(arr, i, store);
                store++;
            }
        }
        swap(arr, store, high);
        return store;
    }

    private static void swap(int[] arr, int i, int j) {
        if (i == j) {
            return;
        }
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
