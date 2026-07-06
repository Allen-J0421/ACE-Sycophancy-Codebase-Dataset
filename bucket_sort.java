import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

interface SortingStrategy<T extends Comparable<T>> {
    void sort(T[] arr);
}

class Bucket<T extends Comparable<T>> {
    private final List<T> values = new ArrayList<>();

    void add(T value) {
        values.add(value);
    }

    void sort() {
        for (int i = 1; i < values.size(); i++) {
            T key = values.get(i);
            int j = i - 1;
            while (j >= 0 && values.get(j).compareTo(key) > 0) {
                values.set(j + 1, values.get(j));
                j--;
            }
            values.set(j + 1, key);
        }
    }

    List<T> getValues() {
        return values;
    }
}

class BucketFactory<T extends Comparable<T>> {
    @SuppressWarnings("unchecked")
    Bucket<T>[] create(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> new Bucket<T>())
                .toArray(Bucket[]::new);
    }
}

record SortingContext<T extends Comparable<T>>(
        BucketFactory<T> bucketFactory,
        Function<T, Integer> indexMapper) {}

class BucketSort<T extends Comparable<T>> implements SortingStrategy<T> {
    private final SortingContext<T> context;

    BucketSort(SortingContext<T> context) {
        this.context = context;
    }

    @Override
    public void sort(T[] arr) {
        Bucket<T>[] buckets = context.bucketFactory().create(arr.length);
        distribute(arr, buckets);
        for (Bucket<T> bucket : buckets) {
            bucket.sort();
        }
        collect(arr, buckets);
    }

    private void distribute(T[] arr, Bucket<T>[] buckets) {
        IntStream.range(0, arr.length)
                .forEach(i -> buckets[context.indexMapper().apply(arr[i])].add(arr[i]));
    }

    private void collect(T[] arr, Bucket<T>[] buckets) {
        List<T> sorted = Arrays.stream(buckets)
                .flatMap(b -> b.getValues().stream())
                .collect(Collectors.toList());
        for (int i = 0; i < arr.length; i++) {
            arr[i] = sorted.get(i);
        }
    }
}

class Main {

    public static void main(String[] args) {
        Float[] arr = {0.897f, 0.565f, 0.656f, 0.1234f, 0.665f, 0.3434f};
        int n = arr.length;
        SortingContext<Float> context = new SortingContext<>(new BucketFactory<Float>(), value -> (int) (n * value));
        SortingStrategy<Float> sorter = new BucketSort<>(context);
        sorter.sort(arr);

        System.out.println("Sorted array is:");
        for (float num : arr) {
            System.out.print(num + " ");
        }
    }
}
