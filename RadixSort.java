import java.util.Arrays;
import java.util.Objects;
import java.util.function.ToIntFunction;

/**
 * Least-significant-digit (LSD) radix sort for {@code int} arrays, and for
 * arbitrary objects keyed by an {@code int}.
 *
 * <p>This is a stable, non-comparison sort. It sorts on one byte at a time
 * (base 256), so a 32-bit {@code int} takes at most four passes regardless of
 * the magnitude of the values, giving O(n) time for fixed-width integers. It
 * sorts in place.
 *
 * <p>The {@code sort(T[], ToIntFunction)} overload extends this to any
 * reference type by deriving an {@code int} key from each element. Because the
 * sort is stable, elements with equal keys keep their original relative order
 * &mdash; the usual reason to radix-sort objects rather than compare them.
 *
 * <p>Either ascending (default) or descending order may be requested via
 * {@link SortOrder}. Descending order is produced by laying the buckets out
 * high-to-low while keeping the scatter stable, so equal keys still retain
 * their original relative order &mdash; it is not a reversed ascending sort.
 *
 * <p>Negative values (including {@link Integer#MIN_VALUE}) are handled directly
 * on the two's-complement representation: when sorting on the most significant
 * byte the sign bit is flipped, which maps the signed range monotonically onto
 * the unsigned range so negatives sort ahead of non-negatives. No bias,
 * widening to {@code long}, or extra arithmetic is required.
 *
 * <p>A single read pass tallies a histogram for every byte up front, and any
 * byte position whose value is identical across all elements is skipped, so
 * narrow-range inputs cost fewer than four passes.
 *
 * <p>This class is a stateless utility and cannot be instantiated.
 */
public final class RadixSort {

    /** Direction in which to sort. */
    public enum SortOrder {
        /** Smallest key first. */
        ASCENDING,
        /** Largest key first. */
        DESCENDING
    }

    /** Number of bits processed per pass. Eight bits = one byte = base 256. */
    private static final int RADIX_BITS = 8;

    /** Number of buckets per pass (2^RADIX_BITS). */
    private static final int RADIX = 1 << RADIX_BITS;

    /** Mask selecting a single byte. */
    private static final int MASK = RADIX - 1;

    /** Passes needed to cover a 32-bit int, one byte at a time. */
    private static final int PASSES = Integer.BYTES;

    /**
     * Below this length, radix sort's allocation and multi-pass overhead
     * outweighs its asymptotic advantage, so a plain insertion sort wins. This
     * is a conservative, tunable threshold; it affects speed only, not
     * correctness.
     */
    private static final int INSERTION_SORT_THRESHOLD = 64;

    private RadixSort() {
        // Utility class: prevent instantiation.
    }

    /**
     * Sorts the given array in ascending order, in place.
     *
     * @param arr the array to sort; {@code null} and arrays of length &lt; 2
     *            are left unchanged
     */
    public static void sort(int[] arr) {
        sort(arr, SortOrder.ASCENDING);
    }

    /**
     * Sorts the given array in the requested order, in place.
     *
     * @param arr   the array to sort; {@code null} and arrays of length &lt; 2
     *              are left unchanged
     * @param order the sort direction; must not be {@code null}
     * @throws NullPointerException if {@code order} is {@code null}
     */
    public static void sort(int[] arr, SortOrder order) {
        Objects.requireNonNull(order, "order");
        if (arr == null || arr.length < 2) {
            return;
        }

        final int n = arr.length;
        final boolean descending = order == SortOrder.DESCENDING;

        if (n <= INSERTION_SORT_THRESHOLD) {
            insertionSort(arr, n, descending);
            return;
        }

        final int[] aux = new int[n];

        // One read pass tallies a histogram for each byte position at once.
        final int[][] counts = new int[PASSES][RADIX];
        for (int value : arr) {
            for (int pass = 0; pass < PASSES; pass++) {
                counts[pass][digit(value, pass)]++;
            }
        }

        // Ping-pong between the caller's array and the scratch buffer.
        int[] src = arr;
        int[] dst = aux;
        for (int pass = 0; pass < PASSES; pass++) {
            final int[] count = counts[pass];

            // If every element shares this byte, the pass cannot change order.
            if (count[digit(src[0], pass)] == n) {
                continue;
            }

            toStartOffsets(count, descending);

            // Stable scatter: equal keys keep their relative order.
            for (int i = 0; i < n; i++) {
                int value = src[i];
                dst[count[digit(value, pass)]++] = value;
            }

            int[] swap = src;
            src = dst;
            dst = swap;
        }

        // If the result landed in the scratch buffer, copy it back.
        if (src != arr) {
            System.arraycopy(src, 0, arr, 0, n);
        }
    }

    /**
     * Returns a sorted copy of {@code arr} in ascending order, leaving the
     * original untouched.
     *
     * @param arr the array to copy and sort; may be {@code null}
     * @return a new sorted array, or {@code null} if {@code arr} is {@code null}
     */
    public static int[] sorted(int[] arr) {
        return sorted(arr, SortOrder.ASCENDING);
    }

    /**
     * Returns a sorted copy of {@code arr} in the requested order, leaving the
     * original untouched.
     *
     * @param arr   the array to copy and sort; may be {@code null}
     * @param order the sort direction; must not be {@code null} unless
     *              {@code arr} is {@code null}
     * @return a new sorted array, or {@code null} if {@code arr} is {@code null}
     */
    public static int[] sorted(int[] arr, SortOrder order) {
        if (arr == null) {
            return null;
        }
        int[] copy = Arrays.copyOf(arr, arr.length);
        sort(copy, order);
        return copy;
    }

    /**
     * Sorts {@code array} in place, in ascending order of the {@code int} key
     * produced by {@code keyExtractor}. The sort is stable.
     *
     * @param array        the array to sort; {@code null} and arrays of length
     *                     &lt; 2 are left unchanged
     * @param keyExtractor maps each element to its sort key; must not be
     *                     {@code null}
     * @param <T>          the element type
     * @throws NullPointerException if {@code keyExtractor} is {@code null}
     */
    public static <T> void sort(T[] array, ToIntFunction<? super T> keyExtractor) {
        sort(array, keyExtractor, SortOrder.ASCENDING);
    }

    /**
     * Sorts {@code array} in place, in the requested order of the {@code int}
     * key produced by {@code keyExtractor}. The sort is stable: elements with
     * equal keys retain their original relative order in both directions.
     *
     * @param array        the array to sort; {@code null} and arrays of length
     *                     &lt; 2 are left unchanged
     * @param keyExtractor maps each element to its sort key; must not be
     *                     {@code null}
     * @param order        the sort direction; must not be {@code null}
     * @param <T>          the element type
     * @throws NullPointerException if {@code keyExtractor} or {@code order} is
     *                              {@code null}
     */
    public static <T> void sort(T[] array, ToIntFunction<? super T> keyExtractor, SortOrder order) {
        Objects.requireNonNull(keyExtractor, "keyExtractor");
        Objects.requireNonNull(order, "order");
        if (array == null || array.length < 2) {
            return;
        }

        final int n = array.length;
        final boolean descending = order == SortOrder.DESCENDING;

        // Extract each key exactly once: the extractor may be non-trivial, and
        // the byte passes below read keys repeatedly.
        final int[] keys = new int[n];
        for (int i = 0; i < n; i++) {
            keys[i] = keyExtractor.applyAsInt(array[i]);
        }

        if (n <= INSERTION_SORT_THRESHOLD) {
            insertionSortByKey(array, keys, n, descending);
            return;
        }

        // copyOf preserves the array's runtime component type; contents are
        // overwritten during scatter, so the copied references are harmless.
        T[] auxArray = Arrays.copyOf(array, n);
        int[] auxKeys = new int[n];

        // One read pass tallies a histogram for each byte position at once.
        final int[][] counts = new int[PASSES][RADIX];
        for (int key : keys) {
            for (int pass = 0; pass < PASSES; pass++) {
                counts[pass][digit(key, pass)]++;
            }
        }

        // Ping-pong elements and their keys together so the two stay aligned.
        T[] srcArray = array;
        T[] dstArray = auxArray;
        int[] srcKeys = keys;
        int[] dstKeys = auxKeys;
        for (int pass = 0; pass < PASSES; pass++) {
            final int[] count = counts[pass];

            // If every element shares this byte, the pass cannot change order.
            if (count[digit(srcKeys[0], pass)] == n) {
                continue;
            }

            toStartOffsets(count, descending);

            // Stable scatter: equal keys keep their relative order.
            for (int i = 0; i < n; i++) {
                int key = srcKeys[i];
                int pos = count[digit(key, pass)]++;
                dstArray[pos] = srcArray[i];
                dstKeys[pos] = key;
            }

            T[] swapArray = srcArray;
            srcArray = dstArray;
            dstArray = swapArray;
            int[] swapKeys = srcKeys;
            srcKeys = dstKeys;
            dstKeys = swapKeys;
        }

        // If the result landed in the scratch buffer, copy it back.
        if (srcArray != array) {
            System.arraycopy(srcArray, 0, array, 0, n);
        }
    }

    /**
     * Returns a sorted copy of {@code array} ordered ascending by
     * {@code keyExtractor}, leaving the original untouched.
     *
     * @param array        the array to copy and sort; may be {@code null}
     * @param keyExtractor maps each element to its sort key; must not be
     *                     {@code null} unless {@code array} is {@code null}
     * @param <T>          the element type
     * @return a new sorted array, or {@code null} if {@code array} is
     *         {@code null}
     */
    public static <T> T[] sorted(T[] array, ToIntFunction<? super T> keyExtractor) {
        return sorted(array, keyExtractor, SortOrder.ASCENDING);
    }

    /**
     * Returns a sorted copy of {@code array} ordered by {@code keyExtractor} in
     * the requested direction, leaving the original untouched.
     *
     * @param array        the array to copy and sort; may be {@code null}
     * @param keyExtractor maps each element to its sort key; must not be
     *                     {@code null} unless {@code array} is {@code null}
     * @param order        the sort direction; must not be {@code null} unless
     *                     {@code array} is {@code null}
     * @param <T>          the element type
     * @return a new sorted array, or {@code null} if {@code array} is
     *         {@code null}
     */
    public static <T> T[] sorted(T[] array, ToIntFunction<? super T> keyExtractor, SortOrder order) {
        if (array == null) {
            return null;
        }
        T[] copy = Arrays.copyOf(array, array.length);
        sort(copy, keyExtractor, order);
        return copy;
    }

    /**
     * Converts a histogram into each bucket's starting index. Buckets are laid
     * out low-to-high for ascending order, or high-to-low for descending, so
     * that a subsequent left-to-right scatter remains stable either way.
     */
    private static void toStartOffsets(int[] count, boolean descending) {
        int start = 0;
        if (descending) {
            for (int bucket = RADIX - 1; bucket >= 0; bucket--) {
                int size = count[bucket];
                count[bucket] = start;
                start += size;
            }
        } else {
            for (int bucket = 0; bucket < RADIX; bucket++) {
                int size = count[bucket];
                count[bucket] = start;
                start += size;
            }
        }
    }

    /** Stable in-place insertion sort over the first {@code n} elements. */
    private static void insertionSort(int[] a, int n, boolean descending) {
        for (int i = 1; i < n; i++) {
            int key = a[i];
            int j = i - 1;
            while (j >= 0 && (descending ? a[j] < key : a[j] > key)) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = key;
        }
    }

    /** Stable insertion sort of {@code array} (and parallel {@code keys}) by key. */
    private static <T> void insertionSortByKey(T[] array, int[] keys, int n, boolean descending) {
        for (int i = 1; i < n; i++) {
            T item = array[i];
            int key = keys[i];
            int j = i - 1;
            while (j >= 0 && (descending ? keys[j] < key : keys[j] > key)) {
                array[j + 1] = array[j];
                keys[j + 1] = keys[j];
                j--;
            }
            array[j + 1] = item;
            keys[j + 1] = key;
        }
    }

    /**
     * Extracts the byte of {@code value} for the given pass (0 = least
     * significant). On the most significant byte the sign bit is flipped so
     * that two's-complement signed values sort in correct order.
     */
    private static int digit(int value, int pass) {
        int b = (value >>> (pass * RADIX_BITS)) & MASK;
        return pass == PASSES - 1 ? b ^ 0x80 : b;
    }

    /** Small demonstration. */
    public static void main(String[] args) {
        int[] arr = { 170, 45, 75, 90, 802, 24, 2, 66 };
        System.out.println("before: " + Arrays.toString(arr));
        sort(arr);
        System.out.println("asc:    " + Arrays.toString(arr));
        sort(arr, SortOrder.DESCENDING);
        System.out.println("desc:   " + Arrays.toString(arr));

        int[] withNegatives = { 4, -1, 0, -802, 75, -75, Integer.MIN_VALUE, Integer.MAX_VALUE };
        System.out.println("mixed:  " + Arrays.toString(RadixSort.sorted(withNegatives)));

        // Generic API: sort objects by an int key, including negatives. Ada and
        // Cy share a score, and the stable sort keeps Ada (listed first) first
        // in both directions.
        record Player(String name, int score) {
            @Override public String toString() { return name + "(" + score + ")"; }
        }
        Player[] players = {
            new Player("Ada", 90), new Player("Ben", -10),
            new Player("Cy", 90), new Player("Dot", 5),
        };
        System.out.println("byKey:  " + Arrays.toString(RadixSort.sorted(players, Player::score)));
        System.out.println("byKey-: " + Arrays.toString(
                RadixSort.sorted(players, Player::score, SortOrder.DESCENDING)));
    }
}
