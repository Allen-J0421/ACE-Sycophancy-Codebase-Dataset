package radixsort;

final class IntPartitions {
    private IntPartitions() {
        // Utility class.
    }

    static long[] negativeMagnitudes(int[] values) {
        long[] negatives = new long[countNegatives(values)];
        int negativeIndex = 0;

        for (int value : values) {
            if (value < 0) {
                negatives[negativeIndex++] = -(long) value;
            }
        }

        return negatives;
    }

    static long[] nonNegativeValues(int[] values) {
        long[] nonNegatives = new long[values.length - countNegatives(values)];
        int nonNegativeIndex = 0;

        for (int value : values) {
            if (value >= 0) {
                nonNegatives[nonNegativeIndex++] = value;
            }
        }

        return nonNegatives;
    }

    static int[] mergeSorted(long[] negatives, long[] nonNegatives) {
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

    private static int countNegatives(int[] values) {
        int negativeCount = 0;
        for (int value : values) {
            if (value < 0) {
                negativeCount++;
            }
        }
        return negativeCount;
    }
}
