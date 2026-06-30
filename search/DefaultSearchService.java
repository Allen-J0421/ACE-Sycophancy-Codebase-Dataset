package search;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

public final class DefaultSearchService<T> implements SearchService<T> {
    private final SearchAlgorithm<T> algorithm;
    private final BiConsumer<List<T>, T> preValidation;

    public DefaultSearchService(SearchAlgorithm<T> algorithm) {
        this(algorithm, SearchServiceBuilder.noPreValidation());
    }

    public DefaultSearchService(SearchAlgorithm<T> algorithm, BiConsumer<List<T>, T> preValidation) {
        this.algorithm = Objects.requireNonNull(algorithm, "algorithm must not be null");
        this.preValidation = Objects.requireNonNull(preValidation, "preValidation must not be null");
    }

    @Override
    public SearchResult search(List<T> sortedValues, T target) {
        Objects.requireNonNull(sortedValues, "sortedValues must not be null");
        preValidation.accept(sortedValues, target);
        return algorithm.search(sortedValues, target);
    }
}
