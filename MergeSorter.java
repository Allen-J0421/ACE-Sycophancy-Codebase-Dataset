/**
 * Generic, stable merge sort over arrays of {@link Comparable} elements.
 *
 * <p>This is the object-array counterpart of {@link MergeSort} (which is
 * specialized for {@code int} to avoid boxing) and uses the same algorithm: a
 * bottom-up, iterative merge sort with an insertion-sort cutoff for short runs.
 * It uses no call stack, runs in {@code O(n log n)} time with {@code O(n)}
 * auxiliary space, and is stable — equal elements keep their relative order.
 *
 * <p>Elements are compared with {@link Comparable#compareTo}; as with
 * {@link java.util.Arrays#sort(Object[])}, {@code null} elements are not
 * supported and cause a {@link NullPointerException}.
 *
 * @param <T> the element type, which must be comparable to itself
 */
public final class MergeSorter<T extends Comparable<T>> implements Sorter<T> {

    /**
     * Initial run length: the array is first split into blocks of this many
     * elements, each sorted with insertion sort, before bottom-up merging begins.
     * Values in the 7–16 range are typical; the exact optimum is hardware-dependent.
     */
    static final int INSERTION_SORT_CUTOFF = 7;

    @Override
    public void sort(T[] array) {
        if (array == null || array.length < 2) {
            return;
        }
        int n = array.length;
        // Generic arrays cannot be created directly; a Comparable[] backing store cast
        // to T[] is the standard idiom (cf. java.util.ArrayList's Object[] backing).
        @SuppressWarnings({"unchecked", "rawtypes"})
        T[] buffer = (T[]) new Comparable[n];

        // Phase 1: turn the array into a sequence of sorted runs of length CUTOFF
        // (the final run may be shorter) using insertion sort.
        for (int low = 0; low < n; low += INSERTION_SORT_CUTOFF) {
            insertionSort(array, low, Math.min(low + INSERTION_SORT_CUTOFF - 1, n - 1));
        }

        // Phase 2: repeatedly merge adjacent runs, doubling the run width each pass,
        // until a single sorted run spans the array. Index arithmetic is done in long
        // to stay correct even for arrays near Integer.MAX_VALUE in length.
        for (long width = INSERTION_SORT_CUTOFF; width < n; width *= 2) {
            for (long low = 0; low < n - width; low += 2 * width) {
                int mid = (int) (low + width - 1);
                int high = (int) Math.min(low + 2 * width - 1, n - 1);
                merge(array, buffer, (int) low, mid, high);
            }
        }
    }

    /**
     * Sorts the small run {@code array[low..high]} (inclusive) in place using
     * insertion sort. Stable: each element stops shifting at the first element
     * that is not strictly greater, so equal elements keep their relative order.
     */
    private void insertionSort(T[] array, int low, int high) {
        for (int i = low + 1; i <= high; i++) {
            T key = array[i];
            int j = i - 1;
            while (j >= low && array[j].compareTo(key) > 0) {
                array[j + 1] = array[j];
                j--;
            }
            array[j + 1] = key;
        }
    }

    /**
     * Merges the two sorted runs {@code array[low..mid]} and
     * {@code array[mid+1..high]} into a single sorted run, using {@code buffer}
     * as scratch space. The merge is stable: equal elements keep their order.
     */
    private void merge(T[] array, T[] buffer, int low, int mid, int high) {
        System.arraycopy(array, low, buffer, low, high - low + 1);

        int left = low;        // next index in the lower run, buffer[low..mid]
        int right = mid + 1;    // next index in the upper run, buffer[mid+1..high]

        for (int dest = low; dest <= high; dest++) {
            if (left > mid) {
                array[dest] = buffer[right++];
            } else if (right > high) {
                array[dest] = buffer[left++];
            } else if (buffer[left].compareTo(buffer[right]) <= 0) {
                array[dest] = buffer[left++];
            } else {
                array[dest] = buffer[right++];
            }
        }
    }
}
