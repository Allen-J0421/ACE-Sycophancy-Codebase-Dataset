import java.util.ArrayList;
import java.util.List;

class Main {
    private static final float[] SAMPLE_VALUES = {
        0.897f, 0.565f, 0.656f, 0.1234f, 0.665f, 0.3434f
    };

    private static final String SORTED_ARRAY_MESSAGE = "Sorted array is:";

    private static List<List<Float>> createBuckets(int count) {
        List<List<Float>> buckets = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            buckets.add(new ArrayList<>());
        }
        return buckets;
    }

    private static int bucketIndex(float value, int bucketCount) {
        return (int) (bucketCount * value);
    }

    private static void distributeIntoBuckets(float[] values, List<List<Float>> buckets) {
        for (float value : values) {
            buckets.get(bucketIndex(value, buckets.size())).add(value);
        }
    }

    private static void sortBuckets(List<List<Float>> buckets) {
        for (List<Float> bucket : buckets) {
            insertionSort(bucket);
        }
    }

    private static void copyBucketsToArray(List<List<Float>> buckets, float[] values) {
        int index = 0;
        for (List<Float> bucket : buckets) {
            for (float value : bucket) {
                values[index++] = value;
            }
        }
    }

    private static void insertIntoSortedPrefix(List<Float> bucket, int unsortedIndex) {
        float value = bucket.get(unsortedIndex);
        int insertionIndex = shiftLargerValuesRight(bucket, value, unsortedIndex - 1);
        bucket.set(insertionIndex, value);
    }

    private static int shiftLargerValuesRight(List<Float> bucket, float value, int startIndex) {
        int index = startIndex;
        while (index >= 0 && bucket.get(index) > value) {
            bucket.set(index + 1, bucket.get(index));
            index--;
        }
        return index + 1;
    }

    public static void insertionSort(List<Float> bucket) {
        for (int i = 1; i < bucket.size(); i++) {
            insertIntoSortedPrefix(bucket, i);
        }
    }

    public static void bucketSort(float[] values) {
        int bucketCount = values.length;

        List<List<Float>> buckets = createBuckets(bucketCount);
        distributeIntoBuckets(values, buckets);
        sortBuckets(buckets);
        copyBucketsToArray(buckets, values);
    }

    private static void printArray(float[] values) {
        for (float value : values) {
            System.out.print(value + " ");
        }
    }

    public static void main(String[] args) {
        float[] values = SAMPLE_VALUES.clone();
        bucketSort(values);

        System.out.println(SORTED_ARRAY_MESSAGE);
        printArray(values);
    }
}
