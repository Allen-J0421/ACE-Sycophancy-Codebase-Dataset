import java.util.ArrayList;
import java.util.List;

public final class Main {

    private Main() {
        // Utility class.
    }

    public static void insertionSort(List<Float> bucket) {
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

    public static void bucketSort(float[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }

        int n = arr.length;
        float min = arr[0];
        float max = arr[0];

        for (float value : arr) {
            if (value < min) {
                min = value;
            }
            if (value > max) {
                max = value;
            }
        }

        if (min == max) {
            return;
        }

        List<List<Float>> buckets = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            buckets.add(new ArrayList<>());
        }

        float range = max - min;
        for (float value : arr) {
            int bucketIndex = (int) (((value - min) / range) * (n - 1));
            buckets.get(bucketIndex).add(value);
        }

        int index = 0;
        for (List<Float> bucket : buckets) {
            insertionSort(bucket);
            for (float value : bucket) {
                arr[index++] = value;
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
