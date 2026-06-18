package prefixsum;

import java.util.Objects;

final class PrefixSumCalculator {

    private PrefixSumCalculator() {
    }

    static int[] compute(int[] values) {
        int[] source = Objects.requireNonNull(values, "values must not be null");
        int[] prefixSums = new int[source.length];
        computeInto(source, prefixSums);
        return prefixSums;
    }

    static void computeInto(int[] values, int[] destination) {
        int[] source = Objects.requireNonNull(values, "values must not be null");
        int[] target = Objects.requireNonNull(destination, "destination must not be null");

        if (source.length != target.length) {
            throw new IllegalArgumentException("destination length must match values length");
        }

        int runningTotal = 0;

        for (int i = 0; i < source.length; i++) {
            runningTotal += source[i];
            target[i] = runningTotal;
        }
    }
}
