import java.util.Arrays;

/**
 * Computes the prefix sum (running total) of an integer array.
 *
 * <p>Given an array {@code [a0, a1, a2, ...]}, the prefix sum is
 * {@code [a0, a0+a1, a0+a1+a2, ...]}, where each element is the sum of all
 * preceding elements up to and including the current index.
 *
 * <p>Declared package-private so the source can live in the repository's
 * {@code prefix_sum_array.java} file (Java requires a {@code public} top-level
 * class to match its filename).
 */
class PrefixSum {

    private PrefixSum() {
        // Utility class; not meant to be instantiated.
    }

    /**
     * Returns the prefix sums of {@code arr}.
     *
     * <p>Results use {@code long} to avoid the silent {@code int} overflow that
     * occurs when running totals exceed {@link Integer#MAX_VALUE}.
     *
     * @param arr the input array; must not be {@code null}
     * @return a new array of the same length where index {@code i} holds the sum
     *         of {@code arr[0..i]}; an empty array yields an empty result
     * @throws NullPointerException if {@code arr} is {@code null}
     */
    static long[] prefixSum(int[] arr) {
        if (arr == null) {
            throw new NullPointerException("arr must not be null");
        }

        long[] sums = new long[arr.length];
        long running = 0;
        for (int i = 0; i < arr.length; i++) {
            running += arr[i];
            sums[i] = running;
        }
        return sums;
    }

    public static void main(String[] args) {
        int[] arr = {10, 20, 10, 5, 15};
        System.out.println(Arrays.toString(prefixSum(arr)));
    }
}
