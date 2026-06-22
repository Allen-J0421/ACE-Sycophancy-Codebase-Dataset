import java.util.ArrayList;
import java.util.List;

class Main {
    private static final float[] SAMPLE_VALUES = {
        0.897f, 0.565f, 0.656f, 0.1234f, 0.665f, 0.3434f
    };

    private static final String SORTED_ARRAY_MESSAGE = "Sorted array is:";

    private static List<Float>[] createBuckets(int count) {
        @SuppressWarnings("unchecked")
        List<Float>[] buckets = new ArrayList[count];
        for (int i = 0; i < count; i++) {
            buckets[i] = new ArrayList<>();
        }
        return buckets;
    }

    private static int bucketIndex(float value, int bucketCount) {
        return (int) (bucketCount * value);
    }

    private static void distributeIntoBuckets(float[] arr, List<Float>[] buckets) {
        for (float value : arr) {
            buckets[bucketIndex(value, buckets.length)].add(value);
        }
    }

    private static void sortBuckets(List<Float>[] buckets) {
        for (List<Float> bucket : buckets) {
            insertionSort(bucket);
        }
    }

    private static void copyBucketsToArray(List<Float>[] buckets, float[] arr) {
        int index = 0;
        for (List<Float> bucket : buckets) {
            for (float value : bucket) {
                arr[index++] = value;
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

    public static void bucketSort(float[] arr) {
        int n = arr.length;

        List<Float>[] buckets = createBuckets(n);
        distributeIntoBuckets(arr, buckets);
        sortBuckets(buckets);
        copyBucketsToArray(buckets, arr);
    }

    private static void printArray(float[] arr) {
        for (float num : arr) {
            System.out.print(num + " ");
        }
    }

    public static void main(String[] args) {
        float[] arr = SAMPLE_VALUES.clone();
        bucketSort(arr);

        System.out.println(SORTED_ARRAY_MESSAGE);
        printArray(arr);
    }
}
