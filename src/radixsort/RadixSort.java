package radixsort;

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

        System.arraycopy(sortedCopy(values), 0, values, 0, values.length);
    }

    public static int[] sortedCopy(int[] values) {
        Objects.requireNonNull(values, "values must not be null");
        if (values.length < 2) {
            return values.clone();
        }

        SignSplit split = splitBySign(values);
        sortAscending(split.negatives);
        sortAscending(split.nonNegatives);
        return merge(split.negatives, split.nonNegatives);
    }

    private static SignSplit splitBySign(int[] values) {
        int negativeCount = countNegatives(values);
        long[] negatives = new long[negativeCount];
        long[] nonNegatives = new long[values.length - negativeCount];

        int negativeIndex = 0;
        int nonNegativeIndex = 0;
        for (int value : values) {
            if (value < 0) {
                negatives[negativeIndex++] = -(long) value;
            } else {
                nonNegatives[nonNegativeIndex++] = value;
            }
        }

        return new SignSplit(negatives, nonNegatives);
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

    private static int[] merge(long[] negatives, long[] nonNegatives) {
        int[] sorted = new int[negatives.length + nonNegatives.length];
        int writeIndex = 0;

        for (int i = negatives.length - 1; i >= 0; i--) {
            sorted[writeIndex++] = (int) -negatives[i];
        }
        for (long value : nonNegatives) {
            sorted[writeIndex++] = (int) value;
        }

        return sorted;
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

    private static final class SignSplit {
        private final long[] negatives;
        private final long[] nonNegatives;

        private SignSplit(long[] negatives, long[] nonNegatives) {
            this.negatives = negatives;
            this.nonNegatives = nonNegatives;
        }
    }
}
