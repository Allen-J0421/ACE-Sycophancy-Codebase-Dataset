/**
 * In-place merge sort for arrays of {@code int}.
 *
 * <p>Merge sort is a stable, divide-and-conquer algorithm with guaranteed
 * {@code O(n log n)} time complexity and {@code O(n)} auxiliary space.
 * This implementation allocates a single scratch buffer for the whole sort
 * rather than allocating fresh temporaries on every merge.
 *
 * <p>The sort is <em>bottom-up</em> and iterative rather than recursive: it first
 * sorts short blocks with insertion sort, then repeatedly merges adjacent sorted
 * runs of doubling width until the whole array is one run. This uses no call stack,
 * so it cannot overflow on large inputs, while keeping the same time and space bounds.
 *
 * <p>As a performance refinement, runs of at most {@link #INSERTION_SORT_CUTOFF}
 * elements are produced with insertion sort instead of being merged from singletons.
 * For small runs insertion sort's low constant factors beat the merge overhead.
 * Insertion sort is itself stable, so overall stability is preserved.
 */
final class MergeSort {

    /**
     * Initial run length: the array is first split into blocks of this many
     * elements, each sorted with insertion sort, before bottom-up merging begins.
     * Values in the 7–16 range are typical; the exact optimum is hardware-dependent.
     */
    static final int INSERTION_SORT_CUTOFF = 7;

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
        int n = array.length;
        int[] buffer = new int[n];

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
    private static void insertionSort(int[] array, int low, int high) {
        for (int i = low + 1; i <= high; i++) {
            int key = array[i];
            int j = i - 1;
            while (j >= low && array[j] > key) {
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
