package bucketsort;

import java.util.Arrays;

final class BucketSet {

    private final Bucket[] buckets;

    private BucketSet(Bucket[] buckets) {
        this.buckets = buckets;
    }

    static BucketSet create(int valueCount, int bucketCount) {
        Bucket[] buckets = new Bucket[bucketCount];
        int initialBucketCapacity = Math.max(1, valueCount / bucketCount);
        for (int i = 0; i < bucketCount; i++) {
            buckets[i] = new Bucket(initialBucketCapacity);
        }
        return new BucketSet(buckets);
    }

    void add(float value, ValueRange range) {
        buckets[range.bucketIndexFor(value, buckets.length)].add(value);
    }

    void sortInto(float[] destination) {
        int writeIndex = 0;

        for (Bucket bucket : buckets) {
            bucket.sort();
            writeIndex = bucket.writeTo(destination, writeIndex);
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
}
