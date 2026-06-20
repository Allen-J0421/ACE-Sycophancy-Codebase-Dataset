package bucketsort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        Range range = Range.from(values);
        if (range.isFlat()) {
            return;
        }

        List<List<Float>> buckets = createBuckets(bucketCount);
        distribute(values, buckets, range);
        gather(values, buckets);
    }

    private static List<List<Float>> createBuckets(int bucketCount) {
        List<List<Float>> buckets = new ArrayList<>(bucketCount);
        for (int i = 0; i < bucketCount; i++) {
            buckets.add(new ArrayList<>());
        }
        return buckets;
    }

    private static void distribute(float[] values, List<List<Float>> buckets, Range range) {
        int bucketCount = buckets.size();

        for (float value : values) {
            buckets.get(range.bucketIndexFor(value, bucketCount)).add(value);
        }
    }

    private static void gather(float[] values, List<List<Float>> buckets) {
        int writeIndex = 0;

        for (List<Float> bucket : buckets) {
            insertionSort(bucket);
            for (float value : bucket) {
                values[writeIndex++] = value;
            }
        }
    }

    private static void insertionSort(List<Float> bucket) {
        for (int i = 1; i < bucket.size(); i++) {
            float key = bucket.get(i);
            int j = i - 1;

            while (j >= 0 && bucket.get(j) > key) {
                bucket.set(j + 1, bucket.get(j));
                j--;
            }

            bucket.set(j + 1, key);
        }
    }

    private static final class Range {
        private final float min;
        private final float max;
        private final float span;

        private Range(float min, float max) {
            this.min = min;
            this.max = max;
            this.span = max - min;
        }

        private static Range from(float[] values) {
            float min = values[0];
            float max = values[0];

            for (float value : values) {
                if (value < min) {
                    min = value;
                }
                if (value > max) {
                    max = value;
                }
            }

            return new Range(min, max);
        }

        private boolean isFlat() {
            return span == 0.0f;
        }

        private int bucketIndexFor(float value, int bucketCount) {
            int bucketIndex = (int) (((value - min) / span) * (bucketCount - 1));
            if (bucketIndex < 0) {
                return 0;
            }
            if (bucketIndex >= bucketCount) {
                return bucketCount - 1;
            }
            return bucketIndex;
        }
    }
}
