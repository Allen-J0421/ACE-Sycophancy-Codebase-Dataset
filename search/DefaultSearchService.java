package search;

import java.util.List;
import java.util.Objects;

public final class DefaultSearchService<T> implements SearchService<T> {
    private final SearchAlgorithm<T> algorithm;

    public DefaultSearchService(SearchAlgorithm<T> algorithm) {
        this.algorithm = Objects.requireNonNull(algorithm, "algorithm must not be null");
    }

    @Override
    public SearchResult search(List<T> sortedValues, T target) {
        return algorithm.search(sortedValues, target);
    }
}
