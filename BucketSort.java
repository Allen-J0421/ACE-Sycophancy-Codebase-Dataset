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

        for (float value : values) {
            validateValue(value);
        }

        Buckets buckets = new Buckets(values.length);
        buckets.distribute(values);
        buckets.sortEach();
        buckets.writeBack(values);
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

    private static final class Buckets {

        private final float[][] valuesByBucket;
        private final int[] sizes;

        private Buckets(int bucketCount) {
            valuesByBucket = new float[bucketCount][];
            sizes = new int[bucketCount];

            for (int i = 0; i < bucketCount; i++) {
                valuesByBucket[i] = new float[0];
            }
        }

        private void distribute(float[] values) {
            for (float value : values) {
                add(value);
            }
        }

        private void sortEach() {
            for (int i = 0; i < valuesByBucket.length; i++) {
                insertionSort(valuesByBucket[i], sizes[i]);
            }
        }

        private void writeBack(float[] target) {
            int targetIndex = 0;
            for (int i = 0; i < valuesByBucket.length; i++) {
                int size = sizes[i];
                System.arraycopy(valuesByBucket[i], 0, target, targetIndex, size);
                targetIndex += size;
            }
        }

        private void add(float value) {
            int bucketIndex = bucketIndexFor(value, valuesByBucket.length);
            int size = sizes[bucketIndex];
            ensureCapacity(bucketIndex, size + 1);
            valuesByBucket[bucketIndex][size] = value;
            sizes[bucketIndex] = size + 1;
        }

        private void ensureCapacity(int bucketIndex, int minCapacity) {
            float[] bucket = valuesByBucket[bucketIndex];
            if (bucket.length >= minCapacity) {
                return;
            }

            int newCapacity = Math.max(1, bucket.length * 2);
            while (newCapacity < minCapacity) {
                newCapacity *= 2;
            }
            valuesByBucket[bucketIndex] = Arrays.copyOf(bucket, newCapacity);
        }
    }
}
