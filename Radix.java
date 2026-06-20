import java.util.Arrays;

/**
 * Utility methods for radix sorting integer arrays in place.
 */
public final class Radix {
    private static final int BITS_PER_BYTE = 8;
    private static final int BYTE_RADIX = 1 << BITS_PER_BYTE;
    private static final int BYTE_MASK = BYTE_RADIX - 1;
    private static final int DECIMAL_RADIX = 10;

    private Radix() {
    }

    /**
     * Returns the maximum value in the first {@code length} elements.
     */
    public static int getMax(int[] values, int length) {
        return getMax(values, 0, length);
    }

    /**
     * Returns the maximum value in {@code values[fromIndex, toIndex)}.
     */
    public static int getMax(int[] values, int fromIndex, int toIndex) {
        validateRange(values, fromIndex, toIndex);

        int length = toIndex - fromIndex;
        if (length == 0) {
            throw new IllegalArgumentException("Cannot find the maximum of an empty range.");
        }

        return maxOf(values, fromIndex, toIndex);
    }

    /**
     * Performs one stable decimal digit counting-sort pass over a non-negative prefix.
     */
    public static void countSort(int[] values, int length, int exponent) {
        validateNonNegativeRange(values, length);
        if (exponent <= 0) {
            throw new IllegalArgumentException("Exponent must be positive.");
        }
        countingSortByDecimalDigit(values, length, exponent);
    }

    /**
     * Sorts the first {@code length} elements.
     *
     * @deprecated Use {@link #radixSort(int[], int)}.
     */
    @Deprecated
    public static void radixsort(int[] values, int length) {
        radixSort(values, length);
    }

    /**
     * Sorts the full array in ascending signed integer order.
     */
    public static void radixSort(int[] values) {
        validateNotNull(values);
        radixSort(values, values.length);
    }

    /**
     * Sorts the first {@code length} elements in ascending signed integer order.
     */
    public static void radixSort(int[] values, int length) {
        radixSort(values, 0, length);
    }

    /**
     * Sorts {@code values[fromIndex, toIndex)} in ascending signed integer order.
     */
    public static void radixSort(int[] values, int fromIndex, int toIndex) {
        validateRange(values, fromIndex, toIndex);

        int length = toIndex - fromIndex;
        if (length < 2) {
            return;
        }

        int[] output = new int[length];
        int[] counts = new int[BYTE_RADIX];

        for (int shift = 0; shift < Integer.SIZE; shift += BITS_PER_BYTE) {
            int currentShift = shift;
            countingSortByDigit(values, fromIndex, length, output, counts, value -> byteAt(value, currentShift));
        }
    }

    /**
     * Prints the first {@code length} elements with the original trailing-space format.
     */
    public static void print(int[] values, int length) {
        print(values, 0, length);
    }

    /**
     * Prints {@code values[fromIndex, toIndex)} with the original trailing-space format.
     */
    public static void print(int[] values, int fromIndex, int toIndex) {
        String formatted = format(values, fromIndex, toIndex);
        if (!formatted.isEmpty()) {
            System.out.print(formatted + " ");
        }
    }

    /**
     * Returns the full array as space-separated values.
     */
    public static String format(int[] values) {
        validateNotNull(values);
        return format(values, values.length);
    }

    /**
     * Returns the first {@code length} elements as space-separated values.
     */
    public static String format(int[] values, int length) {
        return format(values, 0, length);
    }

    /**
     * Returns {@code values[fromIndex, toIndex)} as space-separated values.
     */
    public static String format(int[] values, int fromIndex, int toIndex) {
        validateRange(values, fromIndex, toIndex);
        if (fromIndex == toIndex) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        result.append(values[fromIndex]);
        for (int i = fromIndex + 1; i < toIndex; i++) {
            result.append(' ').append(values[i]);
        }
        return result.toString();
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

    private static void countingSortByDecimalDigit(int[] values, int length, long exponent) {
        int[] output = new int[length];
        int[] counts = new int[DECIMAL_RADIX];

        countingSortByDigit(values, 0, length, output, counts, value -> decimalDigitAt(value, exponent));
    }

    private static void countingSortByDigit(
            int[] values, int fromIndex, int length, int[] output, int[] counts, DigitExtractor digitExtractor) {
        Arrays.fill(counts, 0);

        for (int i = 0; i < length; i++) {
            counts[digitExtractor.digit(values[fromIndex + i])]++;
        }

        for (int i = 1; i < counts.length; i++) {
            counts[i] += counts[i - 1];
        }

        for (int i = length - 1; i >= 0; i--) {
            int value = values[fromIndex + i];
            int digit = digitExtractor.digit(value);
            output[counts[digit] - 1] = value;
            counts[digit]--;
        }

        System.arraycopy(output, 0, values, fromIndex, length);
    }

    private static int byteAt(int value, int shift) {
        return ((value ^ Integer.MIN_VALUE) >>> shift) & BYTE_MASK;
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

    @FunctionalInterface
    private interface DigitExtractor {
        int digit(int value);
    }
}
