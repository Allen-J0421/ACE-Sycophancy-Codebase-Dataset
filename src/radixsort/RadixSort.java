package radixsort;

import java.util.Objects;

public final class RadixSort {
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

        long[] negatives = LongRadixSort.sortedCopy(IntPartitions.negativeMagnitudes(values));
        long[] nonNegatives = LongRadixSort.sortedCopy(IntPartitions.nonNegativeValues(values));
        return IntPartitions.mergeSorted(negatives, nonNegatives);
    }
}
