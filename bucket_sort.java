import java.util.ArrayList;
import java.util.List;

public class Main {

    private static void insertionSort(List<Float> bucket) {
        for (int i = 1; i < bucket.size(); ++i) {
            float key = bucket.get(i);
            int j = i - 1;
            while (j >= 0 && bucket.get(j) > key) {
                bucket.set(j + 1, bucket.get(j));
                j--;
            }
            bucket.set(j + 1, key);
        }
    }

    @SuppressWarnings("unchecked")
    public static void bucketSort(float[] arr) {
        if (arr == null || arr.length == 0) {
            return;
        }
        int n = arr.length;

        List<Float>[] buckets = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            buckets[i] = new ArrayList<>();
        }

        for (float val : arr) {
            int bucketIndex = Math.min((int) (n * val), n - 1);
            buckets[bucketIndex].add(val);
        }

        for (List<Float> bucket : buckets) {
            insertionSort(bucket);
        }

        int index = 0;
        for (List<Float> bucket : buckets) {
            for (float val : bucket) {
                arr[index++] = val;
            }
        }
    }

    public static void main(String[] args) {
        float[] arr = {0.897f, 0.565f, 0.656f, 0.1234f, 0.665f, 0.3434f};
        bucketSort(arr);

        System.out.println("Sorted array is:");
        for (float num : arr) {
            System.out.print(num + " ");
        }
        System.out.println();
    }
}
