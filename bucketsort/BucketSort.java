package bucketsort;

import java.util.Arrays;

public final class BucketSort {

    private BucketSort() {
        // Utility class.
    }

    public static void sort(float[] values) {
        sort(values, values == null ? 0 : values.length);
    }

    public static void sort(float[] values, int bucketCount) {
        if (values == null || values.length < 2) {
            return;
        }
        if (bucketCount < 1) {
            throw new IllegalArgumentException("bucketCount must be positive");
        }

        sortInPlace(values, bucketCount);
    }

    public static float[] sortedCopy(float[] values) {
        return sortedCopy(values, values == null ? 0 : values.length);
    }

    public static float[] sortedCopy(float[] values, int bucketCount) {
        if (values == null) {
            return null;
        }

        float[] copy = Arrays.copyOf(values, values.length);
        sort(copy, bucketCount);
        return copy;
    }

    private static void sortInPlace(float[] values, int bucketCount) {
        ValueRange range = ValueRange.from(values);
        if (range.isFlat()) {
            return;
        }

        BucketSet buckets = BucketSet.create(values.length, bucketCount);
        for (float value : values) {
            buckets.add(value, range);
        }
        buckets.sortInto(values);
    }
}
