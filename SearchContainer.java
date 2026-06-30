final class SearchContainer {
    private final SearchAlgorithm searchAlgorithm;
    private final SearchResultFormatter resultFormatter;

    private SearchContainer(Builder builder) {
        searchAlgorithm = requireConfigured(builder.searchAlgorithm, "searchAlgorithm");
        resultFormatter = requireConfigured(builder.resultFormatter, "resultFormatter");
    }

    static SearchContainer defaultContainer() {
        return builder()
                .withSearchAlgorithm(new BinarySearcher())
                .withResultFormatter(new DefaultSearchResultFormatter())
                .build();
    }

    static Builder builder() {
        return new Builder();
    }

    SearchAlgorithm searchAlgorithm() {
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

    static final class Builder {
        private SearchAlgorithm searchAlgorithm;
        private SearchResultFormatter resultFormatter;

        private Builder() {
        }

        Builder withSearchAlgorithm(SearchAlgorithm searchAlgorithm) {
            this.searchAlgorithm = searchAlgorithm;
            return this;
        }

        Builder withResultFormatter(SearchResultFormatter resultFormatter) {
            this.resultFormatter = resultFormatter;
            return this;
        }

        SearchContainer build() {
            return new SearchContainer(this);
        }
    }
}
