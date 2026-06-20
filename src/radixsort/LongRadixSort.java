package radixsort;

import java.util.Arrays;

final class LongRadixSort {
    private static final int RADIX = 10;

    private LongRadixSort() {
        // Utility class.
    }

    static void sort(long[] values) {
        if (values.length < 2) {
            return;
        }

        long[] sorted = sortedCopy(values);
        System.arraycopy(sorted, 0, values, 0, values.length);
    }

    static long[] sortedCopy(long[] values) {
        if (values.length < 2) {
            return values.clone();
        }

        long[] sorted = values.clone();
        sortInPlace(sorted);
        return sorted;
    }

    private static void sortInPlace(long[] values) {
        Workspace workspace = new Workspace(values.length);
        long max = findMax(values);
        for (long exp = 1; max / exp > 0; exp *= RADIX) {
            countingSortByDigit(values, exp, workspace);
            System.arraycopy(workspace.output, 0, values, 0, values.length);
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

    private static void countingSortByDigit(long[] values, long exp, Workspace workspace) {
        int[] count = workspace.count;
        Arrays.fill(count, 0);

        for (long value : values) {
            count[digitAt(value, exp)]++;
        }

        for (int i = 1; i < RADIX; i++) {
            count[i] += count[i - 1];
        }

        for (int i = values.length - 1; i >= 0; i--) {
            long value = values[i];
            int digit = digitAt(value, exp);
            workspace.output[count[digit] - 1] = value;
            count[digit]--;
        }
    }

    private static int digitAt(long value, long exp) {
        return (int) ((value / exp) % RADIX);
    }

    private static final class Workspace {
        private final long[] output;
        private final int[] count;

        private Workspace(int size) {
            this.output = new long[size];
            this.count = new int[RADIX];
        }
    }
}
