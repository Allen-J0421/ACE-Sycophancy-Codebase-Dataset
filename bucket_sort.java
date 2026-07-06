import java.util.ArrayList;
import java.util.List;

interface SortingStrategy {
    void sort(float[] arr);
}

class Bucket {
    private final List<Float> values = new ArrayList<>();

    void add(float value) {
        values.add(value);
    }

    void sort() {
        for (int i = 1; i < values.size(); i++) {
            float key = values.get(i);
            int j = i - 1;
            while (j >= 0 && values.get(j) > key) {
                values.set(j + 1, values.get(j));
                j--;
            }
            values.set(j + 1, key);
        }
    }

    List<Float> getValues() {
        return values;
    }
}

class BucketSort implements SortingStrategy {

    @Override
    public void sort(float[] arr) {
        Bucket[] buckets = createBuckets(arr.length);
        distribute(arr, buckets);
        for (Bucket bucket : buckets) {
            bucket.sort();
        }
        collect(arr, buckets);
    }

    private Bucket[] createBuckets(int count) {
        Bucket[] buckets = new Bucket[count];
        for (int i = 0; i < count; i++) {
            buckets[i] = new Bucket();
        }
        return buckets;
    }

    private void distribute(float[] arr, Bucket[] buckets) {
        int n = arr.length;
        for (float value : arr) {
            int index = (int) (n * value);
            buckets[index].add(value);
        }
    }

    private void collect(float[] arr, Bucket[] buckets) {
        int index = 0;
        for (Bucket bucket : buckets) {
            for (float value : bucket.getValues()) {
                arr[index++] = value;
            }
        }
    }
}

class Main {

    public static void main(String[] args) {
        float[] arr = {0.897f, 0.565f, 0.656f, 0.1234f, 0.665f, 0.3434f};
        SortingStrategy sorter = new BucketSort();
        sorter.sort(arr);

        System.out.println("Sorted array is:");
        for (float num : arr) {
            System.out.print(num + " ");
        }
    }
}
