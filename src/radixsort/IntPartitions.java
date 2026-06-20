package radixsort;

final class IntPartitions {
    private final long[] negativeMagnitudes;
    private final long[] nonNegativeValues;

    private IntPartitions(long[] negativeMagnitudes, long[] nonNegativeValues) {
        this.negativeMagnitudes = negativeMagnitudes;
        this.nonNegativeValues = nonNegativeValues;
    }

    static IntPartitions split(int[] values) {
        int negativeCount = countNegatives(values);
        long[] negativeMagnitudes = new long[negativeCount];
        long[] nonNegativeValues = new long[values.length - negativeCount];

        int negativeIndex = 0;
        int nonNegativeIndex = 0;
        for (int value : values) {
            if (value < 0) {
                negativeMagnitudes[negativeIndex++] = -(long) value;
            } else {
                nonNegativeValues[nonNegativeIndex++] = value;
            }
        }

        return new IntPartitions(negativeMagnitudes, nonNegativeValues);
    }

    long[] negativeMagnitudes() {
        return negativeMagnitudes;
    }

    long[] nonNegativeValues() {
        return nonNegativeValues;
    }

    int[] mergeSorted(long[] sortedNegativeMagnitudes, long[] sortedNonNegativeValues) {
        int[] sorted = new int[sortedNegativeMagnitudes.length + sortedNonNegativeValues.length];
        int writeIndex = 0;

        for (int i = sortedNegativeMagnitudes.length - 1; i >= 0; i--) {
            sorted[writeIndex++] = (int) -sortedNegativeMagnitudes[i];
        }
        for (long value : sortedNonNegativeValues) {
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
