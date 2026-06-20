import java.util.Arrays;

public class CountingSort {

    /**
     * Sorts the given array in ascending order using counting sort.
     *
     * <p>Runs in O(n + k) time and space, where {@code k} is the range of
     * values (max - min). Handles negative values and is a stable sort.
     *
     * @param arr the array to sort; must not be {@code null}
     * @return a new sorted array (the input is left unmodified)
     * @throws NullPointerException if {@code arr} is {@code null}
     */
    public static int[] countSort(int[] arr) {
        if (arr == null) {
            throw new NullPointerException("arr must not be null");
        }

        int n = arr.length;
        if (n == 0) {
            return new int[0];
        }

        int minVal = arr[0];
        int maxVal = arr[0];
        for (int i = 1; i < n; i++) {
            if (arr[i] < minVal) {
                minVal = arr[i];
            } else if (arr[i] > maxVal) {
                maxVal = arr[i];
            }
        }

        // Index values by their offset from minVal so negatives are supported.
        int range = maxVal - minVal + 1;
        int[] counts = new int[range];
        for (int i = 0; i < n; i++) {
            counts[arr[i] - minVal]++;
        }

        // Prefix sums: counts[i] becomes the end position of value (minVal + i).
        for (int i = 1; i < range; i++) {
            counts[i] += counts[i - 1];
        }

        // Walk right-to-left so equal keys keep their input order (stable).
        int[] sorted = new int[n];
        for (int i = n - 1; i >= 0; i--) {
            int v = arr[i];
            sorted[--counts[v - minVal]] = v;
        }

        return sorted;
    }

    public static void main(String[] args) {
        int[] arr = {2, 5, 3, 0, 2, 3, 0, 3};
        System.out.println(Arrays.toString(countSort(arr)));
    }
}
