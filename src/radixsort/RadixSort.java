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

        IntPartitions partitions = IntPartitions.from(values);
        LongRadixSort.sort(partitions.negatives());
        LongRadixSort.sort(partitions.nonNegatives());
        return partitions.merge();
    }
}
