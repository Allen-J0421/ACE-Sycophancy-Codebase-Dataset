import java.util.Comparator;
import java.util.Objects;

/**
 * A robust, reusable sort built on quicksort (an "introsort").
 *
 * <p>Beyond the textbook algorithm, this implementation hardens the places where
 * a naive quicksort goes wrong in practice:
 *
 * <ul>
 *   <li><b>Guaranteed O(n log n) worst case.</b> The partition depth is capped at
 *       {@code 2*floor(log2 n)}; if a path exceeds it — the signature of an
 *       adversarial "median-of-three killer" input that would otherwise go
 *       quadratic — that sub-range falls back to heapsort. This is the
 *       introsort strategy used by C++'s {@code std::sort}.</li>
 *   <li><b>Bounded stack usage.</b> Recursion always descends into the
 *       <em>smaller</em> partition and loops on the larger one, capping live
 *       recursion depth at O(log n) and preventing the {@link StackOverflowError}
 *       a textbook recursive quicksort throws on large sorted input.</li>
 *   <li><b>Good pivots on ordered data.</b> The pivot is chosen by
 *       median-of-three sampling, so already-sorted and reverse-sorted inputs —
 *       the classic worst cases for a first/last-element pivot — stay fast
 *       without ever reaching the heapsort fallback.</li>
 *   <li><b>Speed on small ranges.</b> Sub-arrays at or below a threshold are
 *       finished with insertion sort, which beats quicksort's bookkeeping
 *       overhead there.</li>
 * </ul>
 *
 * <p>This is a stateless utility class: every entry point is static and operates
 * only on its argument array, so concurrent calls on distinct arrays are safe.
 */
public final class QuickSort {

    /** Sub-ranges of at most this length are sorted with insertion sort. */
    private static final int INSERTION_SORT_THRESHOLD = 16;

    private QuickSort() {
        // Utility class — not instantiable.
    }

    /**
     * The introsort depth limit for an input of {@code length} elements:
     * {@code 2*floor(log2(length))}. Exceeding it signals pathological pivot
     * choices, triggering the heapsort fallback.
     */
    private static int maxDepth(int length) {
        return 2 * (31 - Integer.numberOfLeadingZeros(length));
    }

    // ------------------------------------------------------------------
    // Primitive int[] API (optimized, no boxing)
    // ------------------------------------------------------------------

    /**
     * Sorts the given array of ints into ascending order, in place.
     *
     * @param array the array to sort; must not be {@code null}
     * @throws NullPointerException if {@code array} is {@code null}
     */
    public static void sort(int[] array) {
        Objects.requireNonNull(array, "array must not be null");
        if (array.length > 1) {
            sortRange(array, 0, array.length - 1, maxDepth(array.length));
        }
    }

    private static void sortRange(int[] a, int low, int high, int depthLimit) {
        while (low < high) {
            if (high - low < INSERTION_SORT_THRESHOLD) {
                insertionSort(a, low, high);
                return;
            }
            if (depthLimit == 0) {
                heapSort(a, low, high); // quicksort is misbehaving here — bail out
                return;
            }
            depthLimit--;
            int p = partition(a, low, high);
            // Recurse into the smaller side, then loop on the larger side.
            // This bounds the live recursion depth to O(log n).
            if (p - low < high - p) {
                sortRange(a, low, p - 1, depthLimit);
                low = p + 1;
            } else {
                sortRange(a, p + 1, high, depthLimit);
                high = p - 1;
            }
        }
    }

    /**
     * Median-of-three partition (Sedgewick scheme). Orders {@code a[low]},
     * {@code a[mid]} and {@code a[high]}, parks the median pivot at
     * {@code high - 1} as a sentinel, then partitions {@code [low, high]} around
     * it. Requires {@code high - low >= 2}, which the threshold guarantees.
     *
     * @return the final resting index of the pivot
     */
    private static int partition(int[] a, int low, int high) {
        int mid = low + (high - low) / 2;
        if (a[mid] < a[low]) swap(a, low, mid);
        if (a[high] < a[low]) swap(a, low, high);
        if (a[high] < a[mid]) swap(a, mid, high);
        // Now a[low] <= a[mid] <= a[high]. Stash the pivot just before high;
        // a[low] and a[high] act as sentinels for the inner scans.
        swap(a, mid, high - 1);
        int pivot = a[high - 1];

        int i = low;
        int j = high - 1;
        while (true) {
            while (a[++i] < pivot) { /* advance */ }
            while (a[--j] > pivot) { /* advance */ }
            if (i >= j) {
                break;
            }
            swap(a, i, j);
        }
        swap(a, i, high - 1); // restore the pivot to its sorted position
        return i;
    }

    /**
     * Heapsort over {@code [low, high]} — the introsort fallback that guarantees
     * O(n log n) once quicksort's recursion depth is exceeded. Package-private so
     * the fallback path can be exercised directly by tests.
     */
    static void heapSort(int[] a, int low, int high) {
        int n = high - low + 1;
        for (int root = n / 2 - 1; root >= 0; root--) {
            siftDown(a, low, root, n);
        }
        for (int end = n - 1; end > 0; end--) {
            swap(a, low, low + end);
            siftDown(a, low, 0, end);
        }
    }

    /** Sifts the element at heap index {@code root} down a heap of {@code size} elements based at {@code low}. */
    private static void siftDown(int[] a, int low, int root, int size) {
        while (true) {
            int child = 2 * root + 1;
            if (child >= size) {
                break;
            }
            if (child + 1 < size && a[low + child + 1] > a[low + child]) {
                child++;
            }
            if (a[low + root] >= a[low + child]) {
                break;
            }
            swap(a, low + root, low + child);
            root = child;
        }
    }

    private static void insertionSort(int[] a, int low, int high) {
        for (int i = low + 1; i <= high; i++) {
            int key = a[i];
            int j = i - 1;
            while (j >= low && a[j] > key) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = key;
        }
    }

    private static void swap(int[] a, int i, int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    // ------------------------------------------------------------------
    // Primitive long[] API (optimized, no boxing)
    // ------------------------------------------------------------------

    /**
     * Sorts the given array of longs into ascending order, in place.
     *
     * @param array the array to sort; must not be {@code null}
     * @throws NullPointerException if {@code array} is {@code null}
     */
    public static void sort(long[] array) {
        Objects.requireNonNull(array, "array must not be null");
        if (array.length > 1) {
            sortRange(array, 0, array.length - 1, maxDepth(array.length));
        }
    }

    private static void sortRange(long[] a, int low, int high, int depthLimit) {
        while (low < high) {
            if (high - low < INSERTION_SORT_THRESHOLD) {
                insertionSort(a, low, high);
                return;
            }
            if (depthLimit == 0) {
                heapSort(a, low, high);
                return;
            }
            depthLimit--;
            int p = partition(a, low, high);
            if (p - low < high - p) {
                sortRange(a, low, p - 1, depthLimit);
                low = p + 1;
            } else {
                sortRange(a, p + 1, high, depthLimit);
                high = p - 1;
            }
        }
    }

    private static int partition(long[] a, int low, int high) {
        int mid = low + (high - low) / 2;
        if (a[mid] < a[low]) swap(a, low, mid);
        if (a[high] < a[low]) swap(a, low, high);
        if (a[high] < a[mid]) swap(a, mid, high);
        swap(a, mid, high - 1);
        long pivot = a[high - 1];

        int i = low;
        int j = high - 1;
        while (true) {
            while (a[++i] < pivot) { /* advance */ }
            while (a[--j] > pivot) { /* advance */ }
            if (i >= j) {
                break;
            }
            swap(a, i, j);
        }
        swap(a, i, high - 1);
        return i;
    }

    /** Heapsort fallback for {@code long[]}; package-private for white-box testing. */
    static void heapSort(long[] a, int low, int high) {
        int n = high - low + 1;
        for (int root = n / 2 - 1; root >= 0; root--) {
            siftDown(a, low, root, n);
        }
        for (int end = n - 1; end > 0; end--) {
            swap(a, low, low + end);
            siftDown(a, low, 0, end);
        }
    }

    private static void siftDown(long[] a, int low, int root, int size) {
        while (true) {
            int child = 2 * root + 1;
            if (child >= size) {
                break;
            }
            if (child + 1 < size && a[low + child + 1] > a[low + child]) {
                child++;
            }
            if (a[low + root] >= a[low + child]) {
                break;
            }
            swap(a, low + root, low + child);
            root = child;
        }
    }

    private static void insertionSort(long[] a, int low, int high) {
        for (int i = low + 1; i <= high; i++) {
            long key = a[i];
            int j = i - 1;
            while (j >= low && a[j] > key) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = key;
        }
    }

    private static void swap(long[] a, int i, int j) {
        long temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    // ------------------------------------------------------------------
    // Primitive double[] API (optimized, no boxing)
    // ------------------------------------------------------------------

    /**
     * Sorts the given array of doubles into ascending order, in place.
     *
     * <p>Ordering follows {@link Double#compare}, matching {@link java.util.Arrays#sort(double[])}:
     * {@code -0.0} sorts before {@code 0.0} and {@code NaN} sorts after all other values.
     *
     * @param array the array to sort; must not be {@code null}
     * @throws NullPointerException if {@code array} is {@code null}
     */
    public static void sort(double[] array) {
        Objects.requireNonNull(array, "array must not be null");
        if (array.length > 1) {
            sortRange(array, 0, array.length - 1, maxDepth(array.length));
        }
    }

    private static void sortRange(double[] a, int low, int high, int depthLimit) {
        while (low < high) {
            if (high - low < INSERTION_SORT_THRESHOLD) {
                insertionSort(a, low, high);
                return;
            }
            if (depthLimit == 0) {
                heapSort(a, low, high);
                return;
            }
            depthLimit--;
            int p = partition(a, low, high);
            if (p - low < high - p) {
                sortRange(a, low, p - 1, depthLimit);
                low = p + 1;
            } else {
                sortRange(a, p + 1, high, depthLimit);
                high = p - 1;
            }
        }
    }

    private static int partition(double[] a, int low, int high) {
        int mid = low + (high - low) / 2;
        if (Double.compare(a[mid], a[low]) < 0) swap(a, low, mid);
        if (Double.compare(a[high], a[low]) < 0) swap(a, low, high);
        if (Double.compare(a[high], a[mid]) < 0) swap(a, mid, high);
        swap(a, mid, high - 1);
        double pivot = a[high - 1];

        int i = low;
        int j = high - 1;
        while (true) {
            while (Double.compare(a[++i], pivot) < 0) { /* advance */ }
            while (Double.compare(a[--j], pivot) > 0) { /* advance */ }
            if (i >= j) {
                break;
            }
            swap(a, i, j);
        }
        swap(a, i, high - 1);
        return i;
    }

    /** Heapsort fallback for {@code double[]}; package-private for white-box testing. */
    static void heapSort(double[] a, int low, int high) {
        int n = high - low + 1;
        for (int root = n / 2 - 1; root >= 0; root--) {
            siftDown(a, low, root, n);
        }
        for (int end = n - 1; end > 0; end--) {
            swap(a, low, low + end);
            siftDown(a, low, 0, end);
        }
    }

    private static void siftDown(double[] a, int low, int root, int size) {
        while (true) {
            int child = 2 * root + 1;
            if (child >= size) {
                break;
            }
            if (child + 1 < size && Double.compare(a[low + child + 1], a[low + child]) > 0) {
                child++;
            }
            if (Double.compare(a[low + root], a[low + child]) >= 0) {
                break;
            }
            swap(a, low + root, low + child);
            root = child;
        }
    }

    private static void insertionSort(double[] a, int low, int high) {
        for (int i = low + 1; i <= high; i++) {
            double key = a[i];
            int j = i - 1;
            while (j >= low && Double.compare(a[j], key) > 0) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = key;
        }
    }

    private static void swap(double[] a, int i, int j) {
        double temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    // ------------------------------------------------------------------
    // Primitive float[] API (optimized, no boxing)
    // ------------------------------------------------------------------

    /**
     * Sorts the given array of floats into ascending order, in place.
     *
     * <p>Ordering follows {@link Float#compare}, matching {@link java.util.Arrays#sort(float[])}:
     * {@code -0.0f} sorts before {@code 0.0f} and {@code NaN} sorts after all other values.
     *
     * @param array the array to sort; must not be {@code null}
     * @throws NullPointerException if {@code array} is {@code null}
     */
    public static void sort(float[] array) {
        Objects.requireNonNull(array, "array must not be null");
        if (array.length > 1) {
            sortRange(array, 0, array.length - 1, maxDepth(array.length));
        }
    }

    private static void sortRange(float[] a, int low, int high, int depthLimit) {
        while (low < high) {
            if (high - low < INSERTION_SORT_THRESHOLD) {
                insertionSort(a, low, high);
                return;
            }
            if (depthLimit == 0) {
                heapSort(a, low, high);
                return;
            }
            depthLimit--;
            int p = partition(a, low, high);
            if (p - low < high - p) {
                sortRange(a, low, p - 1, depthLimit);
                low = p + 1;
            } else {
                sortRange(a, p + 1, high, depthLimit);
                high = p - 1;
            }
        }
    }

    private static int partition(float[] a, int low, int high) {
        int mid = low + (high - low) / 2;
        if (Float.compare(a[mid], a[low]) < 0) swap(a, low, mid);
        if (Float.compare(a[high], a[low]) < 0) swap(a, low, high);
        if (Float.compare(a[high], a[mid]) < 0) swap(a, mid, high);
        swap(a, mid, high - 1);
        float pivot = a[high - 1];

        int i = low;
        int j = high - 1;
        while (true) {
            while (Float.compare(a[++i], pivot) < 0) { /* advance */ }
            while (Float.compare(a[--j], pivot) > 0) { /* advance */ }
            if (i >= j) {
                break;
            }
            swap(a, i, j);
        }
        swap(a, i, high - 1);
        return i;
    }

    /** Heapsort fallback for {@code float[]}; package-private for white-box testing. */
    static void heapSort(float[] a, int low, int high) {
        int n = high - low + 1;
        for (int root = n / 2 - 1; root >= 0; root--) {
            siftDown(a, low, root, n);
        }
        for (int end = n - 1; end > 0; end--) {
            swap(a, low, low + end);
            siftDown(a, low, 0, end);
        }
    }

    private static void siftDown(float[] a, int low, int root, int size) {
        while (true) {
            int child = 2 * root + 1;
            if (child >= size) {
                break;
            }
            if (child + 1 < size && Float.compare(a[low + child + 1], a[low + child]) > 0) {
                child++;
            }
            if (Float.compare(a[low + root], a[low + child]) >= 0) {
                break;
            }
            swap(a, low + root, low + child);
            root = child;
        }
    }

    private static void insertionSort(float[] a, int low, int high) {
        for (int i = low + 1; i <= high; i++) {
            float key = a[i];
            int j = i - 1;
            while (j >= low && Float.compare(a[j], key) > 0) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = key;
        }
    }

    private static void swap(float[] a, int i, int j) {
        float temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    // ------------------------------------------------------------------
    // Generic object API
    // ------------------------------------------------------------------

    /**
     * Sorts the given array into ascending order according to the elements'
     * {@linkplain Comparable natural ordering}, in place.
     *
     * @param array the array to sort; must not be {@code null}
     * @param <T>   a comparable element type
     * @throws NullPointerException if {@code array} is {@code null}
     */
    public static <T extends Comparable<? super T>> void sort(T[] array) {
        sort(array, Comparator.naturalOrder());
    }

    /**
     * Sorts the given array in place using the supplied comparator.
     *
     * @param array      the array to sort; must not be {@code null}
     * @param comparator the ordering to impose; must not be {@code null}
     * @param <T>        the element type
     * @throws NullPointerException if {@code array} or {@code comparator} is {@code null}
     */
    public static <T> void sort(T[] array, Comparator<? super T> comparator) {
        Objects.requireNonNull(array, "array must not be null");
        Objects.requireNonNull(comparator, "comparator must not be null");
        if (array.length > 1) {
            sortRange(array, 0, array.length - 1, maxDepth(array.length), comparator);
        }
    }

    private static <T> void sortRange(T[] a, int low, int high, int depthLimit, Comparator<? super T> cmp) {
        while (low < high) {
            if (high - low < INSERTION_SORT_THRESHOLD) {
                insertionSort(a, low, high, cmp);
                return;
            }
            if (depthLimit == 0) {
                heapSort(a, low, high, cmp);
                return;
            }
            depthLimit--;
            int p = partition(a, low, high, cmp);
            if (p - low < high - p) {
                sortRange(a, low, p - 1, depthLimit, cmp);
                low = p + 1;
            } else {
                sortRange(a, p + 1, high, depthLimit, cmp);
                high = p - 1;
            }
        }
    }

    private static <T> int partition(T[] a, int low, int high, Comparator<? super T> cmp) {
        int mid = low + (high - low) / 2;
        if (cmp.compare(a[mid], a[low]) < 0) swap(a, low, mid);
        if (cmp.compare(a[high], a[low]) < 0) swap(a, low, high);
        if (cmp.compare(a[high], a[mid]) < 0) swap(a, mid, high);
        swap(a, mid, high - 1);
        T pivot = a[high - 1];

        int i = low;
        int j = high - 1;
        while (true) {
            while (cmp.compare(a[++i], pivot) < 0) { /* advance */ }
            while (cmp.compare(a[--j], pivot) > 0) { /* advance */ }
            if (i >= j) {
                break;
            }
            swap(a, i, j);
        }
        swap(a, i, high - 1);
        return i;
    }

    /** Generic counterpart of {@link #heapSort(int[], int, int)}; package-private for testing. */
    static <T> void heapSort(T[] a, int low, int high, Comparator<? super T> cmp) {
        int n = high - low + 1;
        for (int root = n / 2 - 1; root >= 0; root--) {
            siftDown(a, low, root, n, cmp);
        }
        for (int end = n - 1; end > 0; end--) {
            swap(a, low, low + end);
            siftDown(a, low, 0, end, cmp);
        }
    }

    private static <T> void siftDown(T[] a, int low, int root, int size, Comparator<? super T> cmp) {
        while (true) {
            int child = 2 * root + 1;
            if (child >= size) {
                break;
            }
            if (child + 1 < size && cmp.compare(a[low + child + 1], a[low + child]) > 0) {
                child++;
            }
            if (cmp.compare(a[low + root], a[low + child]) >= 0) {
                break;
            }
            swap(a, low + root, low + child);
            root = child;
        }
    }

    private static <T> void insertionSort(T[] a, int low, int high, Comparator<? super T> cmp) {
        for (int i = low + 1; i <= high; i++) {
            T key = a[i];
            int j = i - 1;
            while (j >= low && cmp.compare(a[j], key) > 0) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = key;
        }
    }

    private static <T> void swap(T[] a, int i, int j) {
        T temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }
}
