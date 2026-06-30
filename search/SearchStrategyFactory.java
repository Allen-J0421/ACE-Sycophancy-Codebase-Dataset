package search;

import java.util.Comparator;
import java.util.Objects;

public final class SearchStrategyFactory {
    private SearchStrategyFactory() {
    }

    public static <T extends Comparable<? super T>> SearchAlgorithm<T> binarySearch() {
        return BinarySearchAlgorithm.naturalOrder();
    }

    public static <T> SearchAlgorithm<T> binarySearch(Comparator<? super T> comparator) {
        return new BinarySearchAlgorithm<T>(Objects.requireNonNull(comparator, "comparator must not be null"));
    }

    public static <T> SearchAlgorithm<T> linearSearch() {
        return new LinearSearchAlgorithm<T>();
    }

    public static <T> SearchServiceBuilder<T> serviceBuilder(SearchAlgorithm<T> algorithm) {
        return SearchServiceBuilder.of(algorithm);
    }
}
