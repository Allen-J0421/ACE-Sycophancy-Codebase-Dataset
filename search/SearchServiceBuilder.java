package search;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

public final class SearchServiceBuilder<T> {
    private final SearchAlgorithm<T> algorithm;
    private BiConsumer<List<T>, T> preValidation = noPreValidation();

    private SearchServiceBuilder(SearchAlgorithm<T> algorithm) {
        this.algorithm = Objects.requireNonNull(algorithm, "algorithm must not be null");
    }

    public static <T> SearchServiceBuilder<T> of(SearchAlgorithm<T> algorithm) {
        return new SearchServiceBuilder<T>(algorithm);
    }

    public SearchServiceBuilder<T> preValidation(BiConsumer<List<T>, T> preValidation) {
        this.preValidation = Objects.requireNonNull(preValidation, "preValidation must not be null");
        return this;
    }

    public SearchService<T> build() {
        return new DefaultSearchService<T>(algorithm, preValidation);
    }

    static <T> BiConsumer<List<T>, T> noPreValidation() {
        return (values, target) -> {
        };
    }
}
