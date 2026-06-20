import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class BucketSort {

    private BucketSort() {
        // Utility class.
    }

    public static void sort(float[] values) {
        if (values == null || values.length < 2) {
            return;
        }

        sortInPlace(values);
    }

    public static float[] sortedCopy(float[] values) {
        if (values == null) {
            return null;
        }

        float[] copy = Arrays.copyOf(values, values.length);
        sort(copy);
        return copy;
    }

    private static void sortInPlace(float[] values) {
        Range range = findRange(values);
        if (range.isFlat()) {
            return;
        }

        List<List<Float>> buckets = createBuckets(values.length);
        distribute(values, buckets, range);
        gather(values, buckets);
    }

    private static Range findRange(float[] values) {
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

    private static List<List<Float>> createBuckets(int bucketCount) {
        List<List<Float>> buckets = new ArrayList<>(bucketCount);
        for (int i = 0; i < bucketCount; i++) {
            buckets.add(new ArrayList<>());
        }
        return buckets;
    }

    private static void distribute(float[] values, List<List<Float>> buckets, Range range) {
        int bucketCount = buckets.size();
        float span = range.max - range.min;

        for (float value : values) {
            int bucketIndex = bucketIndexFor(value, range, span, bucketCount);
            buckets.get(bucketIndex).add(value);
        }
    }

    private static int bucketIndexFor(float value, Range range, float span, int bucketCount) {
        int bucketIndex = (int) (((value - range.min) / span) * (bucketCount - 1));
        if (bucketIndex < 0) {
            return 0;
        }
        if (bucketIndex >= bucketCount) {
            return bucketCount - 1;
        }
        return bucketIndex;
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

        private Range(float min, float max) {
            this.min = min;
            this.max = max;
        }

        private boolean isFlat() {
            return min == max;
        }
    }
}
