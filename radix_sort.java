import java.util.Arrays;
import java.util.Objects;

final class RadixSort {
    private static final int RADIX = 10;
    private static final int[] SAMPLE_VALUES = { 170, 45, 75, 90, 802, 24, 2, 66 };

    private RadixSort() {
        // Utility class.
    }

    static void sort(int[] values) {
        Objects.requireNonNull(values, "values must not be null");
        if (values.length < 2) {
            return;
        }

        int max = findMax(values);
        for (long exp = 1; max / exp > 0; exp *= RADIX) {
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

    private static void countingSortByDigit(int[] values, long exp) {
        int[] output = new int[values.length];
        int[] count = new int[RADIX];

        for (int value : values) {
            count[digitAt(value, exp)]++;
        }

        for (int i = 1; i < RADIX; i++) {
            count[i] += count[i - 1];
        }

        for (int i = values.length - 1; i >= 0; i--) {
            int value = values[i];
            int digit = digitAt(value, exp);
            output[count[digit] - 1] = value;
            count[digit]--;
        }

        System.arraycopy(output, 0, values, 0, values.length);
    }

    private static int digitAt(int value, long exp) {
        return (int) ((value / exp) % RADIX);
    }

    private static void printValues(int[] values) {
        System.out.println(Arrays.toString(values));
    }

    public static void main(String[] args) {
        int[] values = SAMPLE_VALUES.clone();

        sort(values);
        printValues(values);
    }
}
