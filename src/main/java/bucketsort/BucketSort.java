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

    private static final class BucketSet {
        private final Bucket[] buckets;

        private BucketSet(Bucket[] buckets) {
            this.buckets = buckets;
        }

        private static BucketSet create(int valueCount, int bucketCount) {
            Bucket[] buckets = new Bucket[bucketCount];
            int initialBucketCapacity = Math.max(1, valueCount / bucketCount);
            for (int i = 0; i < bucketCount; i++) {
                buckets[i] = new Bucket(initialBucketCapacity);
            }
            return new BucketSet(buckets);
        }

        private void add(float value, ValueRange range) {
            buckets[range.bucketIndexFor(value, buckets.length)].add(value);
        }

        private void sortInto(float[] destination) {
            int writeIndex = 0;

            for (Bucket bucket : buckets) {
                bucket.sort();
                writeIndex = bucket.writeTo(destination, writeIndex);
            }
        }
    }

    private static final class Bucket {
        private float[] values;
        private int size;

        private Bucket(int initialCapacity) {
            this.values = new float[initialCapacity];
        }

        private void add(float value) {
            ensureCapacity(size + 1);
            values[size++] = value;
        }

        private void sort() {
            for (int i = 1; i < size; i++) {
                float key = values[i];
                int j = i - 1;

                while (j >= 0 && values[j] > key) {
                    values[j + 1] = values[j];
                    j--;
                }

                values[j + 1] = key;
            }
        }

        private int writeTo(float[] destination, int startIndex) {
            System.arraycopy(values, 0, destination, startIndex, size);
            return startIndex + size;
        }

        private void ensureCapacity(int requiredCapacity) {
            if (requiredCapacity <= values.length) {
                return;
            }

            int newCapacity = Math.max(requiredCapacity, values.length * 2);
            values = Arrays.copyOf(values, newCapacity);
        }
    }

    private static final class ValueRange {
        private final float min;
        private final float span;

        private ValueRange(float min, float max) {
            this.min = min;
            this.span = max - min;
        }

        private static ValueRange from(float[] values) {
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

            return new ValueRange(min, max);
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
