import java.util.Arrays;

public final class Radix {
    private static final int BITS_PER_BYTE = 8;
    private static final int BYTE_RADIX = 1 << BITS_PER_BYTE;
    private static final int BYTE_MASK = BYTE_RADIX - 1;
    private static final int DECIMAL_RADIX = 10;

    private Radix() {
    }

    public static int getMax(int[] values, int length) {
        validateRange(values, length);

        if (length == 0) {
            throw new IllegalArgumentException("Cannot find the maximum of an empty range.");
        }

        return maxOf(values, length);
    }

    public static void countSort(int[] values, int length, int exponent) {
        validateNonNegativeRange(values, length);
        if (exponent <= 0) {
            throw new IllegalArgumentException("Exponent must be positive.");
        }
        countingSortByDecimalDigit(values, length, exponent);
    }

    @Deprecated
    public static void radixsort(int[] values, int length) {
        radixSort(values, length);
    }

    public static void radixSort(int[] values) {
        validateNotNull(values);
        radixSort(values, values.length);
    }

    public static void radixSort(int[] values, int length) {
        validateRange(values, length);
        if (length < 2) {
            return;
        }

        int[] output = new int[length];
        int[] counts = new int[BYTE_RADIX];

        for (int shift = 0; shift < Integer.SIZE; shift += BITS_PER_BYTE) {
            countingSortByByte(values, length, shift, output, counts);
        }
    }

    public static void print(int[] values, int length) {
        validateRange(values, length);

        for (int i = 0; i < length; i++) {
            System.out.print(values[i] + " ");
        }
    }

    private static int maxOf(int[] values, int length) {
        int max = values[0];
        for (int i = 1; i < length; i++) {
            if (values[i] > max) {
                max = values[i];
            }
        }
        return max;
    }

    private static void countingSortByByte(
            int[] values, int length, int shift, int[] output, int[] counts) {
        Arrays.fill(counts, 0);

        for (int i = 0; i < length; i++) {
            counts[byteAt(values[i], shift)]++;
        }

        for (int i = 1; i < BYTE_RADIX; i++) {
            counts[i] += counts[i - 1];
        }

        for (int i = length - 1; i >= 0; i--) {
            int digit = byteAt(values[i], shift);
            output[counts[digit] - 1] = values[i];
            counts[digit]--;
        }

        System.arraycopy(output, 0, values, 0, length);
    }

    private static int byteAt(int value, int shift) {
        return ((value ^ Integer.MIN_VALUE) >>> shift) & BYTE_MASK;
    }

    private static void countingSortByDecimalDigit(int[] values, int length, long exponent) {
        int[] output = new int[length];
        int[] counts = new int[DECIMAL_RADIX];

        for (int i = 0; i < length; i++) {
            counts[decimalDigitAt(values[i], exponent)]++;
        }

        for (int i = 1; i < DECIMAL_RADIX; i++) {
            counts[i] += counts[i - 1];
        }

        for (int i = length - 1; i >= 0; i--) {
            int digit = decimalDigitAt(values[i], exponent);
            output[counts[digit] - 1] = values[i];
            counts[digit]--;
        }

        System.arraycopy(output, 0, values, 0, length);
    }

    private static int decimalDigitAt(int value, long exponent) {
        return (int) ((value / exponent) % DECIMAL_RADIX);
    }

    private static void validateRange(int[] values, int length) {
        validateNotNull(values);
        if (length < 0 || length > values.length) {
            throw new IllegalArgumentException("Length must be between 0 and values.length.");
        }
    }

    private static void validateNotNull(int[] values) {
        if (values == null) {
            throw new IllegalArgumentException("Values cannot be null.");
        }
    }

    private static void validateNonNegativeRange(int[] values, int length) {
        validateRange(values, length);
        validateNonNegative(values, length);
    }

    private static void validateNonNegative(int[] values, int length) {
        for (int i = 0; i < length; i++) {
            if (values[i] < 0) {
                throw new IllegalArgumentException("Decimal digit count sort only supports non-negative integers.");
            }
        }
    }
}
