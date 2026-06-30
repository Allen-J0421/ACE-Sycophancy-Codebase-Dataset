import java.util.Comparator;

final class SearchContainer<T> {
    private final SearchAlgorithm<T> searchAlgorithm;
    private final SearchResultFormatter resultFormatter;

    private SearchContainer(Builder<T> builder) {
        searchAlgorithm = requireConfigured(builder.searchAlgorithm, "searchAlgorithm");
        resultFormatter = requireConfigured(builder.resultFormatter, "resultFormatter");
    }

    static SearchContainer<Integer> defaultContainer() {
        return SearchContainer.<Integer>naturalOrderContainer();
    }

    static <T extends Comparable<? super T>> SearchContainer<T> naturalOrderContainer() {
        return SearchContainer.<T>naturalOrderContainer(SearchStrategies.<T>iterativeBinarySearch());
    }

    static <T extends Comparable<? super T>> SearchContainer<T> naturalOrderContainer(
            SearchStrategy<T> strategy) {
        return SearchContainer.<T>builder()
                .withSearchAlgorithm(BinarySearcher.<T>naturalOrder(strategy))
                .withResultFormatter(new DefaultSearchResultFormatter())
                .build();
    }

    static <T> SearchContainer<T> comparatorContainer(Comparator<? super T> comparator) {
        return SearchContainer.<T>comparatorContainer(
                comparator,
                SearchStrategies.<T>iterativeBinarySearch());
    }

    static <T> SearchContainer<T> comparatorContainer(
            Comparator<? super T> comparator,
            SearchStrategy<T> strategy) {
        return SearchContainer.<T>builder()
                .withSearchAlgorithm(new BinarySearcher<T>(comparator, strategy))
                .withResultFormatter(new DefaultSearchResultFormatter())
                .build();
    }

    static <T> Builder<T> builder() {
        return new Builder<T>();
    }

    SearchAlgorithm<T> searchAlgorithm() {
        return searchAlgorithm;
    }

    SearchResultFormatter resultFormatter() {
        return resultFormatter;
    }

    private static <T> T requireConfigured(T dependency, String name) {
        if (dependency == null) {
            throw new IllegalArgumentException(name + " must not be null");
        }

        return dependency;
    }

    static final class Builder<T> {
        private SearchAlgorithm<T> searchAlgorithm;
        private SearchResultFormatter resultFormatter;

        private Builder() {
        }

        Builder<T> withSearchAlgorithm(SearchAlgorithm<T> searchAlgorithm) {
            this.searchAlgorithm = searchAlgorithm;
            return this;
        }

        Builder<T> withResultFormatter(SearchResultFormatter resultFormatter) {
            this.resultFormatter = resultFormatter;
            return this;
        }

        SearchContainer<T> build() {
            return new SearchContainer<T>(this);
        }
    }
}
