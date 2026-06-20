import java.util.Arrays;

public final class Radix {
    private static final int BITS_PER_BYTE = 8;
    private static final int BYTE_RADIX = 1 << BITS_PER_BYTE;
    private static final int BYTE_MASK = BYTE_RADIX - 1;
    private static final int DECIMAL_RADIX = 10;

    private Radix() {
    }

    public static int getMax(int[] values, int length) {
        return getMax(values, 0, length);
    }

    public static int getMax(int[] values, int fromIndex, int toIndex) {
        validateRange(values, fromIndex, toIndex);

        int length = toIndex - fromIndex;
        if (length == 0) {
            throw new IllegalArgumentException("Cannot find the maximum of an empty range.");
        }

        return maxOf(values, fromIndex, toIndex);
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
        radixSort(values, 0, length);
    }

    public static void radixSort(int[] values, int fromIndex, int toIndex) {
        validateRange(values, fromIndex, toIndex);

        int length = toIndex - fromIndex;
        if (length < 2) {
            return;
        }

        int[] output = new int[length];
        int[] counts = new int[BYTE_RADIX];

        for (int shift = 0; shift < Integer.SIZE; shift += BITS_PER_BYTE) {
            countingSortByByte(values, fromIndex, length, shift, output, counts);
        }
    }

    public static void print(int[] values, int length) {
        print(values, 0, length);
    }

    public static void print(int[] values, int fromIndex, int toIndex) {
        validateRange(values, fromIndex, toIndex);

        for (int i = fromIndex; i < toIndex; i++) {
            System.out.print(values[i] + " ");
        }
    }

    private static int maxOf(int[] values, int fromIndex, int toIndex) {
        int max = values[fromIndex];
        for (int i = fromIndex + 1; i < toIndex; i++) {
            if (values[i] > max) {
                max = values[i];
            }
        }
        return max;
    }

    private static void countingSortByByte(
            int[] values, int fromIndex, int length, int shift, int[] output, int[] counts) {
        Arrays.fill(counts, 0);

        for (int i = 0; i < length; i++) {
            counts[byteAt(values[fromIndex + i], shift)]++;
        }

        for (int i = 1; i < BYTE_RADIX; i++) {
            counts[i] += counts[i - 1];
        }

        for (int i = length - 1; i >= 0; i--) {
            int value = values[fromIndex + i];
            int digit = byteAt(value, shift);
            output[counts[digit] - 1] = value;
            counts[digit]--;
        }

        System.arraycopy(output, 0, values, fromIndex, length);
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
        validateRange(values, 0, length);
    }

    private static void validateRange(int[] values, int fromIndex, int toIndex) {
        validateNotNull(values);
        if (fromIndex < 0 || toIndex < fromIndex || toIndex > values.length) {
            throw new IllegalArgumentException("Range must satisfy 0 <= fromIndex <= toIndex <= values.length.");
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
