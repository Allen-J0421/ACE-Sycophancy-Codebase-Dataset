public final class Radix {
    private static final int RADIX = 10;

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
        validateSortableRange(values, length);
        if (exponent <= 0) {
            throw new IllegalArgumentException("Exponent must be positive.");
        }
        countingSortByDigit(values, length, exponent);
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
        validateSortableRange(values, length);
        if (length < 2) {
            return;
        }

        int max = maxOf(values, length);

        for (long exponent = 1; max / exponent > 0; exponent *= RADIX) {
            countingSortByDigit(values, length, exponent);
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

    private static void countingSortByDigit(int[] values, int length, long exponent) {
        int[] output = new int[length];
        int[] counts = new int[RADIX];

        for (int i = 0; i < length; i++) {
            counts[digitAt(values[i], exponent)]++;
        }

        for (int i = 1; i < RADIX; i++) {
            counts[i] += counts[i - 1];
        }

        for (int i = length - 1; i >= 0; i--) {
            int digit = digitAt(values[i], exponent);
            output[counts[digit] - 1] = values[i];
            counts[digit]--;
        }

        System.arraycopy(output, 0, values, 0, length);
    }

    private static int digitAt(int value, long exponent) {
        return (int) ((value / exponent) % RADIX);
    }

    private static void validateSortableRange(int[] values, int length) {
        validateRange(values, length);
        validateNonNegative(values, length);
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

    private static void validateNonNegative(int[] values, int length) {
        for (int i = 0; i < length; i++) {
            if (values[i] < 0) {
                throw new IllegalArgumentException("Radix sort only supports non-negative integers.");
            }
        }
    }

    public static void main(String[] args) {
        int[] values = {170, 45, 75, 90, 802, 24, 2, 66};

        radixSort(values);
        print(values, values.length);
    }
}
