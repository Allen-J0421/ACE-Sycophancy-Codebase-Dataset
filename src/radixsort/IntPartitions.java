package radixsort;

final class IntPartitions {
    private final long[] negatives;
    private final long[] nonNegatives;

    private IntPartitions(long[] negatives, long[] nonNegatives) {
        this.negatives = negatives;
        this.nonNegatives = nonNegatives;
    }

    static IntPartitions from(int[] values) {
        long[] negatives = new long[countNegatives(values)];
        long[] nonNegatives = new long[values.length - negatives.length];

        int negativeIndex = 0;
        int nonNegativeIndex = 0;
        for (int value : values) {
            if (value < 0) {
                negatives[negativeIndex++] = -(long) value;
            } else {
                nonNegatives[nonNegativeIndex++] = value;
            }
        }

        return new IntPartitions(negatives, nonNegatives);
    }

    long[] negatives() {
        return negatives;
    }

    long[] nonNegatives() {
        return nonNegatives;
    }

    int[] merge() {
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
