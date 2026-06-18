import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class SorterFactory {
    private static final Map<String, SorterBuilder<?>> REGISTRY = new HashMap<>();

    static {
        register("optimized-merge", IntegerMergeSorterBuilder.INSTANCE);
        register("insertion", IntegerInsertionBuilder.INSTANCE);
    }

    public static <T extends Comparable<T>> Sorter<T> create(String algorithm) {
        SorterBuilder<T> builder = (SorterBuilder<T>) REGISTRY.get(algorithm);
        if (builder == null) {
            throw new IllegalArgumentException("Unknown sorting algorithm: " + algorithm);
        }
        return builder.build();
    }

    public static <T extends Comparable<T>> Sorter<T> create(String algorithm, SortProfile profile) {
        SorterBuilder<T> builder = (SorterBuilder<T>) REGISTRY.get(algorithm);
        if (builder == null) {
            throw new IllegalArgumentException("Unknown sorting algorithm: " + algorithm);
        }
        return builder.buildWithProfile(profile);
    }

    public static <T extends Comparable<T>> Sorter<T> createOptimized() {
        return new OptimizedMergeSort<T>(SortProfile.BALANCED.create());
    }

    public static <T extends Comparable<T>> Sorter<T> createFast() {
        return new OptimizedMergeSort<T>(SortProfile.SPEED.create());
    }

    public static <T extends Comparable<T>> Sorter<T> createSafe() {
        return new OptimizedMergeSort<T>(SortProfile.SAFE.create());
    }

    public static <T extends Comparable<T>> Sorter<T> createDebug() {
        return new OptimizedMergeSort<T>(SortProfile.DEBUG.create());
    }

    public static void register(String name, SorterBuilder<?> builder) {
        REGISTRY.put(name, builder);
    }

    public static java.util.Set<String> getAvailableAlgorithms() {
        return REGISTRY.keySet();
    }

    interface SorterBuilder<T> {
        Sorter<T> build();
        Sorter<T> buildWithProfile(SortProfile profile);
    }

    static class IntegerMergeSorterBuilder implements SorterBuilder<Integer> {
        static final IntegerMergeSorterBuilder INSTANCE = new IntegerMergeSorterBuilder();

        @Override
        public Sorter<Integer> build() {
            return new OptimizedMergeSort<Integer>(SortProfile.BALANCED.create());
        }

        @Override
        public Sorter<Integer> buildWithProfile(SortProfile profile) {
            return new OptimizedMergeSort<Integer>(profile.create());
        }
    }

    static class IntegerInsertionBuilder implements SorterBuilder<Integer> {
        static final IntegerInsertionBuilder INSTANCE = new IntegerInsertionBuilder();

        @Override
        public Sorter<Integer> build() {
            SortConfiguration<Integer> config = SortConfiguration.<Integer>builder()
                    .withComparator(Integer::compareTo)
                    .withThreshold(Integer.MAX_VALUE)
                    .build();
            return new OptimizedMergeSort<Integer>(config,
                    new InsertionSortStrategy<Integer>(Integer::compareTo, null));
        }

        @Override
        public Sorter<Integer> buildWithProfile(SortProfile profile) {
            return build();
        }
    }
}
