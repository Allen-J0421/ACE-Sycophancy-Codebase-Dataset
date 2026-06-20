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

        BucketSet buckets = new BucketSet(values.length);
        distribute(values, buckets);
        buckets.sortEach();
        buckets.writeBack(values);
    }

    private static void distribute(float[] values, BucketSet buckets) {
        for (float value : values) {
            validateValue(value);
            buckets.add(value);
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

    private static final class BucketSet {

        private final Bucket[] buckets;

        private BucketSet(int bucketCount) {
            buckets = new Bucket[bucketCount];

            for (int i = 0; i < bucketCount; i++) {
                buckets[i] = new Bucket();
            }
        }

        private void sortEach() {
            for (Bucket bucket : buckets) {
                bucket.sort();
            }
        }

        private void writeBack(float[] target) {
            int targetIndex = 0;
            for (Bucket bucket : buckets) {
                targetIndex = bucket.writeTo(target, targetIndex);
            }
        }

        private void add(float value) {
            int bucketIndex = bucketIndexFor(value, buckets.length);
            buckets[bucketIndex].add(value);
        }
    }

    private static final class Bucket {

        private float[] values = new float[0];
        private int size;

        private void add(float value) {
            ensureCapacity(size + 1);
            values[size] = value;
            size++;
        }

        private void sort() {
            insertionSort(values, size);
        }

        private int writeTo(float[] target, int targetIndex) {
            System.arraycopy(values, 0, target, targetIndex, size);
            return targetIndex + size;
        }

        private void ensureCapacity(int minCapacity) {
            if (values.length >= minCapacity) {
                return;
            }

            int newCapacity = Math.max(1, values.length * 2);
            while (newCapacity < minCapacity) {
                newCapacity *= 2;
            }
            values = Arrays.copyOf(values, newCapacity);
        }
    }
}
