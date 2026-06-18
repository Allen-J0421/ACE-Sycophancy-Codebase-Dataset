/**
 * In-place merge sort for arrays of {@code int}.
 *
 * <p>Merge sort is a stable, divide-and-conquer algorithm with guaranteed
 * {@code O(n log n)} time complexity and {@code O(n)} auxiliary space.
 * This implementation allocates a single scratch buffer for the whole sort
 * rather than allocating fresh temporaries on every merge.
 */
final class MergeSort {

    private MergeSort() {
        // Utility class; not meant to be instantiated.
    }

    /**
     * Sorts the given array into ascending order.
     *
     * @param array the array to sort; modified in place. A {@code null} or
     *              single-element array is already sorted and left untouched.
     */
    public static void sort(int[] array) {
        if (array == null || array.length < 2) {
            return;
        }
        int[] buffer = new int[array.length];
        sort(array, buffer, 0, array.length - 1);
    }

    /**
     * Recursively sorts {@code array[low..high]} (inclusive) using a shared buffer.
     */
    private static void sort(int[] array, int[] buffer, int low, int high) {
        if (low >= high) {
            return;
        }
        int mid = low + (high - low) / 2;
        sort(array, buffer, low, mid);
        sort(array, buffer, mid + 1, high);
        merge(array, buffer, low, mid, high);
    }

    /**
     * Merges the two sorted runs {@code array[low..mid]} and
     * {@code array[mid+1..high]} into a single sorted run, using {@code buffer}
     * as scratch space. The merge is stable: equal elements keep their order.
     */
    private static void merge(int[] array, int[] buffer, int low, int mid, int high) {
        System.arraycopy(array, low, buffer, low, high - low + 1);

        int left = low;        // next index in the lower run, buffer[low..mid]
        int right = mid + 1;    // next index in the upper run, buffer[mid+1..high]

        for (int dest = low; dest <= high; dest++) {
            if (left > mid) {
                array[dest] = buffer[right++];
            } else if (right > high) {
                array[dest] = buffer[left++];
            } else if (buffer[left] <= buffer[right]) {
                array[dest] = buffer[left++];
            } else {
                array[dest] = buffer[right++];
            }
        }
    }

    public static void main(String[] args) {
        int[] array = {38, 27, 43, 10};

        sort(array);

        StringBuilder sb = new StringBuilder();
        for (int value : array) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(value);
        }
        System.out.println(sb);
    }
}
