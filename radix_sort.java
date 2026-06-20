import java.util.*;

final class RadixSort {
    private static final int RADIX = 10;

    private RadixSort() {
        // Utility class.
    }

    static void sort(int[] values) {
        if (values == null) {
            throw new IllegalArgumentException("values must not be null");
        }
        if (values.length < 2) {
            return;
        }

        int max = findMax(values);
        for (int exp = 1; max / exp > 0; exp *= RADIX) {
            countingSortByDigit(values, exp);
        }
    }

    private static int findMax(int[] values) {
        int max = values[0];
        for (int i = 1; i < values.length; i++) {
            if (values[i] > max) {
                max = values[i];
            }
        }
        return max;
    }

    private static void countingSortByDigit(int[] values, int exp) {
        int[] output = new int[values.length];
        int[] count = new int[RADIX];

        for (int value : values) {
            count[(value / exp) % RADIX]++;
        }

        for (int i = 1; i < RADIX; i++) {
            count[i] += count[i - 1];
        }

        for (int i = values.length - 1; i >= 0; i--) {
            int value = values[i];
            int digit = (value / exp) % RADIX;
            output[count[digit] - 1] = value;
            count[digit]--;
        }

        System.arraycopy(output, 0, values, 0, values.length);
    }

    private static void printValues(int[] values) {
        System.out.println(Arrays.toString(values));
    }

    public static void main(String[] args) {
        int[] values = { 170, 45, 75, 90, 802, 24, 2, 66 };

        sort(values);
        printValues(values);
    }
}
