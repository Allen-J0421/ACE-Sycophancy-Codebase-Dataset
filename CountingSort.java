import java.util.Arrays;

public class CountingSort {

    /**
     * Sorts the given array in ascending order using counting sort.
     *
     * <p>Runs in O(n + k) time and space, where {@code k} is the range of
     * values (max - min). Handles negative values.
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
        for (int v : arr) {
            counts[v - minVal]++;
        }

        // Emit each value in ascending order, once per recorded occurrence.
        int[] sorted = new int[n];
        int idx = 0;
        for (int i = 0; i < range; i++) {
            int value = minVal + i;
            for (int c = counts[i]; c > 0; c--) {
                sorted[idx++] = value;
            }
        }

        return sorted;
    }

    public static void main(String[] args) {
        int[] arr = {2, 5, 3, 0, 2, 3, 0, 3};
        System.out.println(Arrays.toString(countSort(arr)));
    }
}
