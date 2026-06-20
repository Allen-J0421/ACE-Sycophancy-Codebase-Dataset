import java.util.Objects;

public final class RadixSort {
    private static final int RADIX = 10;

    private RadixSort() {
        // Utility class.
    }

    public static void sort(int[] values) {
        Objects.requireNonNull(values, "values must not be null");
        if (values.length < 2) {
            return;
        }

        int[] sorted = sortedCopy(values);
        System.arraycopy(sorted, 0, values, 0, values.length);
    }

    public static int[] sortedCopy(int[] values) {
        Objects.requireNonNull(values, "values must not be null");
        if (values.length < 2) {
            return values.clone();
        }

        int negativeCount = countNegatives(values);
        long[] negatives = new long[negativeCount];
        long[] nonNegatives = new long[values.length - negativeCount];
        partitionBySign(values, negatives, nonNegatives);

        sortAscending(negatives);
        sortAscending(nonNegatives);

        int[] sorted = new int[values.length];
        mergePartitions(negatives, nonNegatives, sorted);
        return sorted;
    }

    private static int countNegatives(int[] values) {
        int negativeCount = 0;
        for (int value : values) {
            if (value < 0) {
                negativeCount++;
            }
        }
        return negativeCount;
    }

    private static void partitionBySign(int[] values, long[] negatives, long[] nonNegatives) {
        int negativeIndex = 0;
        int nonNegativeIndex = 0;
        for (int value : values) {
            if (value < 0) {
                negatives[negativeIndex++] = -(long) value;
            } else {
                nonNegatives[nonNegativeIndex++] = value;
            }
        }
    }

    private static void mergePartitions(long[] negatives, long[] nonNegatives, int[] destination) {
        int writeIndex = 0;
        for (int i = negatives.length - 1; i >= 0; i--) {
            destination[writeIndex++] = (int) -negatives[i];
        }
        for (long value : nonNegatives) {
            destination[writeIndex++] = (int) value;
        }
    }

    private static void sortAscending(long[] values) {
        if (values.length < 2) {
            return;
        }

        long max = findMax(values);
        for (long exp = 1; max / exp > 0; exp *= RADIX) {
            countingSortByDigit(values, exp);
        }
    }

    private static long findMax(long[] values) {
        long max = values[0];
        for (int i = 1; i < values.length; i++) {
            if (values[i] > max) {
                max = values[i];
            }
        }
        return max;
    }

    private static void countingSortByDigit(long[] values, long exp) {
        long[] output = new long[values.length];
        int[] count = new int[RADIX];

        for (long value : values) {
            count[digitAt(value, exp)]++;
        }

        for (int i = 1; i < RADIX; i++) {
            count[i] += count[i - 1];
        }

        for (int i = values.length - 1; i >= 0; i--) {
            long value = values[i];
            int digit = digitAt(value, exp);
            output[count[digit] - 1] = value;
            count[digit]--;
        }

        System.arraycopy(output, 0, values, 0, values.length);
    }

    private static int digitAt(long value, long exp) {
        return (int) ((value / exp) % RADIX);
    }
}
