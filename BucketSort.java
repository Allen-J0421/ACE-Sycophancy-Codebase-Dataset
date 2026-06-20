import java.util.Arrays;
import java.util.Objects;

public final class BucketSort {

    private static final float MIN_SUPPORTED_VALUE = 0.0f;
    private static final float MAX_SUPPORTED_VALUE = 1.0f;

    private BucketSort() {
    }

    public static void sort(float[] values) {
        Objects.requireNonNull(values, "values");
        sortInPlace(values);
    }

    public static float[] sortedCopy(float[] values) {
        Objects.requireNonNull(values, "values");

        float[] copy = Arrays.copyOf(values, values.length);
        sortInPlace(copy);
        return copy;
    }

    private static void sortInPlace(float[] values) {
        if (values.length == 0) {
            return;
        }

        int[] bucketSizes = countValuesPerBucket(values);
        float[][] buckets = createBuckets(bucketSizes);
        distribute(values, buckets);
        sortBuckets(buckets, bucketSizes);
        mergeBuckets(buckets, bucketSizes, values);
    }

    private static int[] countValuesPerBucket(float[] values) {
        int[] bucketSizes = new int[values.length];

        for (float value : values) {
            validateValue(value);
            bucketSizes[bucketIndexFor(value, bucketSizes.length)]++;
        }

        return bucketSizes;
    }

    private static float[][] createBuckets(int[] bucketSizes) {
        float[][] buckets = new float[bucketSizes.length][];
        for (int i = 0; i < bucketSizes.length; i++) {
            buckets[i] = new float[bucketSizes[i]];
        }
        return buckets;
    }

    private static void distribute(float[] values, float[][] buckets) {
        int[] insertionIndexes = new int[buckets.length];
        for (float value : values) {
            int bucketIndex = bucketIndexFor(value, buckets.length);
            buckets[bucketIndex][insertionIndexes[bucketIndex]++] = value;
        }
    }

    private static void sortBuckets(float[][] buckets, int[] bucketSizes) {
        for (int i = 0; i < buckets.length; i++) {
            insertionSort(buckets[i], bucketSizes[i]);
        }
    }

    private static void mergeBuckets(float[][] buckets, int[] bucketSizes, float[] target) {
        int targetIndex = 0;
        for (int i = 0; i < buckets.length; i++) {
            int bucketSize = bucketSizes[i];
            System.arraycopy(buckets[i], 0, target, targetIndex, bucketSize);
            targetIndex += bucketSize;
        }
    }

    private static void insertionSort(float[] bucket, int size) {
        for (int i = 1; i < size; i++) {
            float currentValue = bucket[i];
            int j = i - 1;
            while (j >= 0 && bucket[j] > currentValue) {
                bucket[j + 1] = bucket[j];
                j--;
            }
            bucket[j + 1] = currentValue;
        }
    }

    private static int bucketIndexFor(float value, int bucketCount) {
        return (int) (bucketCount * value);
    }

    private static void validateValue(float value) {
        if (!isSupportedValue(value)) {
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

    private static boolean isSupportedValue(float value) {
        return Float.isFinite(value)
            && value >= MIN_SUPPORTED_VALUE
            && value < MAX_SUPPORTED_VALUE;
    }
}
