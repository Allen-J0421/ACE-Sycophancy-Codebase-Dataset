import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class BucketSort {

    private static final float MIN_SUPPORTED_VALUE = 0.0f;
    private static final float MAX_SUPPORTED_VALUE = 1.0f;

    private BucketSort() {
    }

    public static void sort(float[] values) {
        float[] sortedValues = sortedCopy(values);
        System.arraycopy(sortedValues, 0, values, 0, values.length);
    }

    public static float[] sortedCopy(float[] values) {
        Objects.requireNonNull(values, "values");

        float[] copy = Arrays.copyOf(values, values.length);

        if (copy.length == 0) {
            return copy;
        }

        List<List<Float>> buckets = createBuckets(copy.length);
        distributeValues(copy, buckets);
        sortBuckets(buckets);
        mergeBuckets(buckets, copy);
        return copy;
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
            int bucketIndex = bucketIndexFor(value, bucketCount);
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

    private static int bucketIndexFor(float value, int bucketCount) {
        return (int) (bucketCount * value);
    }

    private static void validateValue(float value) {
        if (value < MIN_SUPPORTED_VALUE || value >= MAX_SUPPORTED_VALUE) {
            throw new IllegalArgumentException(
                "Bucket sort expects values in the range ["
                    + MIN_SUPPORTED_VALUE
                    + ", "
                    + MAX_SUPPORTED_VALUE
                    + "): "
                    + value
            );
        }
    }
}
