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

class BucketOrchestrator<T extends Comparable<T>> {
    private final SortingContext<T> context;

    BucketOrchestrator(SortingContext<T> context) {
        this.context = context;
    }

    void distribute(T[] arr, Bucket<T>[] buckets) {
        IntStream.range(0, arr.length)
                .forEach(i -> buckets[context.indexMapper().apply(arr[i])].add(arr[i]));
    }

    void collect(T[] arr, Bucket<T>[] buckets) {
        List<T> sorted = Arrays.stream(buckets)
                .flatMap(b -> b.getValues().stream())
                .collect(Collectors.toList());
        for (int i = 0; i < arr.length; i++) {
            arr[i] = sorted.get(i);
        }
    }
}

class BucketSort<T extends Comparable<T>> implements SortingStrategy<T> {
    private final SortingContext<T> context;
    private final BucketOrchestrator<T> orchestrator;

    BucketSort(SortingContext<T> context) {
        this.context = context;
        this.orchestrator = new BucketOrchestrator<>(context);
    }

    @Override
    public void sort(T[] arr) {
        Bucket<T>[] buckets = context.bucketFactory().create(arr.length);
        orchestrator.distribute(arr, buckets);
        for (Bucket<T> bucket : buckets) {
            bucket.sort();
        }
        orchestrator.collect(arr, buckets);
    }
}

class SortRunner<T extends Comparable<T>> {
    private final T[] data;
    private final SortingStrategy<T> sorter;

    SortRunner(T[] data, SortingStrategy<T> sorter) {
        this.data = data;
        this.sorter = sorter;
    }

    void run() {
        sorter.sort(data);
        System.out.println("Sorted array is:");
        for (T value : data) {
            System.out.print(value + " ");
        }
    }
}

class Main {

    public static void main(String[] args) {
        Float[] data = {0.897f, 0.565f, 0.656f, 0.1234f, 0.665f, 0.3434f};
        int n = data.length;
        SortingContext<Float> context = new SortingContext<>(new BucketFactory<Float>(), value -> (int) (n * value));
        new SortRunner<>(data, new BucketSort<>(context)).run();
    }
}
