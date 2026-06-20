import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class BucketSort {

    private BucketSort() {
    }

    public static void sort(float[] values) {
        Objects.requireNonNull(values, "values");

        if (values.length == 0) {
            return;
        }

        List<List<Float>> buckets = createBuckets(values.length);
        distributeValues(values, buckets);
        sortBuckets(buckets);
        mergeBuckets(buckets, values);
    }

    private static List<List<Float>> createBuckets(int bucketCount) {
        List<List<Float>> buckets = new ArrayList<>(bucketCount);
        for (int i = 0; i < bucketCount; i++) {
            buckets.add(new ArrayList<>());
        }
        return buckets;
    }

    private static void distributeValues(float[] values, List<List<Float>> buckets) {
        int bucketCount = buckets.size();
        for (float value : values) {
            validateValue(value);
            int bucketIndex = (int) (bucketCount * value);
            buckets.get(bucketIndex).add(value);
        }
    }

    private static void sortBuckets(List<List<Float>> buckets) {
        for (List<Float> bucket : buckets) {
            insertionSort(bucket);
        }
    }

    private static void mergeBuckets(List<List<Float>> buckets, float[] values) {
        int index = 0;
        for (List<Float> bucket : buckets) {
            for (float value : bucket) {
                values[index++] = value;
            }
        }
    }

    private static void insertionSort(List<Float> bucket) {
        for (int i = 1; i < bucket.size(); i++) {
            float currentValue = bucket.get(i);
            int j = i - 1;
            while (j >= 0 && bucket.get(j) > currentValue) {
                bucket.set(j + 1, bucket.get(j));
                j--;
            }
            bucket.set(j + 1, currentValue);
        }
    }

    private static void validateValue(float value) {
        if (value < 0.0f || value >= 1.0f) {
            throw new IllegalArgumentException(
                "Bucket sort expects values in the range [0.0, 1.0): " + value
            );
        }
    }
}
