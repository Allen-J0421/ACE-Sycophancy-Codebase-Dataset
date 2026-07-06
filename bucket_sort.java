import java.util.ArrayList;
import java.util.List;

class BucketSort {

    public void sort(float[] arr) {
        List<Float>[] buckets = createBuckets(arr.length);
        distribute(arr, buckets);
        sortBuckets(buckets);
        collect(arr, buckets);
    }

    private List<Float>[] createBuckets(int count) {
        List<Float>[] buckets = new ArrayList[count];
        for (int i = 0; i < count; i++) {
            buckets[i] = new ArrayList<>();
        }
        return buckets;
    }

    private void distribute(float[] arr, List<Float>[] buckets) {
        int n = arr.length;
        for (float value : arr) {
            int index = (int) (n * value);
            buckets[index].add(value);
        }
    }

    private void sortBuckets(List<Float>[] buckets) {
        for (List<Float> bucket : buckets) {
            insertionSort(bucket);
        }
    }

    private void collect(float[] arr, List<Float>[] buckets) {
        int index = 0;
        for (List<Float> bucket : buckets) {
            for (float value : bucket) {
                arr[index++] = value;
            }
        }
    }

    private void insertionSort(List<Float> bucket) {
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
}

class Main {

    public static void main(String[] args) {
        float[] arr = {0.897f, 0.565f, 0.656f, 0.1234f, 0.665f, 0.3434f};
        new BucketSort().sort(arr);

        System.out.println("Sorted array is:");
        for (float num : arr) {
            System.out.print(num + " ");
        }
    }
}
