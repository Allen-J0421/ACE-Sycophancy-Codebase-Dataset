import java.util.Comparator;

final class BinarySearcher<T> implements SearchAlgorithm<T> {
    private final Comparator<? super T> comparator;
    private final SearchStrategy<T> strategy;

    BinarySearcher(Comparator<? super T> comparator) {
        this(comparator, SearchStrategies.<T>iterativeBinarySearch());
    }

    BinarySearcher(Comparator<? super T> comparator, SearchStrategy<T> strategy) {
        if (comparator == null) {
            throw new IllegalArgumentException("comparator must not be null");
        }

        if (strategy == null) {
            throw new IllegalArgumentException("strategy must not be null");
        }

        this.comparator = comparator;
        this.strategy = strategy;
    }

    static <T extends Comparable<? super T>> BinarySearcher<T> naturalOrder() {
        return new BinarySearcher<T>(Comparator.naturalOrder());
    }

    static <T extends Comparable<? super T>> BinarySearcher<T> naturalOrder(SearchStrategy<T> strategy) {
        return new BinarySearcher<T>(Comparator.naturalOrder(), strategy);
    }

    @Override
    public SearchResult search(T[] sortedArray, T target) {
        validateInput(sortedArray, target);

        return strategy.search(sortedArray, target, comparator);
    }

    private static <T> void validateInput(T[] sortedArray, T target) {
        if (sortedArray == null) {
            throw new IllegalArgumentException("sortedArray must not be null");
        }

        if (target == null) {
            throw new IllegalArgumentException("target must not be null");
        }
    }
}
