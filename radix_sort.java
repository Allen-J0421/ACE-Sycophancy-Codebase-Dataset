import java.util.Arrays;

class RadixSort {

    private static final int RADIX = 10;

    public static void sort(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }

        int min = arr[0];
        int max = arr[0];
        for (int val : arr) {
            if (val < min) min = val;
            if (val > max) max = val;
        }

        // Shift values into non-negative range so the algorithm works on negatives.
        int offset = min < 0 ? -min : 0;
        if (offset > 0) {
            for (int i = 0; i < arr.length; i++) arr[i] += offset;
            max += offset;
        }

        for (int exp = 1; max / exp > 0; exp *= RADIX) {
            countSort(arr, exp);
        }

        if (offset > 0) {
            for (int i = 0; i < arr.length; i++) arr[i] -= offset;
        }
    }

    private static void countSort(int[] arr, int exp) {
        int[] output = new int[arr.length];
        int[] count = new int[RADIX];

        for (int val : arr) {
            count[(val / exp) % RADIX]++;
        }

        for (int i = 1; i < RADIX; i++) {
            count[i] += count[i - 1];
        }

        for (int i = arr.length - 1; i >= 0; i--) {
            int digit = (arr[i] / exp) % RADIX;
            output[count[digit] - 1] = arr[i];
            count[digit]--;
        }

        System.arraycopy(output, 0, arr, 0, arr.length);
    }

    public static void main(String[] args) {
        int[] arr = { 170, 45, -3, 75, 90, 802, 24, 2, -50, 66 };
        sort(arr);
        System.out.println(Arrays.toString(arr));
    }
}
