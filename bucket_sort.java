import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

class BucketFactory {
    Bucket[] create(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> new Bucket())
                .toArray(Bucket[]::new);
    }
}

class BucketSort implements SortingStrategy {
    private final BucketFactory bucketFactory = new BucketFactory();

    @Override
    public void sort(float[] arr) {
        Bucket[] buckets = bucketFactory.create(arr.length);
        distribute(arr, buckets);
        for (Bucket bucket : buckets) {
            bucket.sort();
        }
        collect(arr, buckets);
    }

    private void distribute(float[] arr, Bucket[] buckets) {
        int n = arr.length;
        IntStream.range(0, n)
                .forEach(i -> buckets[(int) (n * arr[i])].add(arr[i]));
    }

    private void collect(float[] arr, Bucket[] buckets) {
        List<Float> sorted = Arrays.stream(buckets)
                .flatMap(b -> b.getValues().stream())
                .collect(Collectors.toList());
        for (int i = 0; i < arr.length; i++) {
            arr[i] = sorted.get(i);
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
