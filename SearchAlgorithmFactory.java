final class SearchAlgorithmFactory {
    private SearchAlgorithmFactory() {
    }

    static <T extends Comparable<? super T>> SearchAlgorithm<T> create(SearchType searchType) {
        return create(searchType, false);
    }

    static <T extends Comparable<? super T>> SearchAlgorithm<T> create(SearchType searchType, boolean loggingEnabled) {
        return create(searchType, loggingEnabled, new DefaultSearchRequestValidator<T>());
    }

    static <T extends Comparable<? super T>> SearchAlgorithm<T> create(
            SearchType searchType,
            boolean loggingEnabled,
            SearchRequestValidator<T> validator) {
        SearchAlgorithm<T> algorithm = createBase(searchType);
        if (validator != null) {
            algorithm = new ValidatingSearchAlgorithm<>(algorithm, validator);
        }

        if (loggingEnabled) {
            return new LoggingSearchAlgorithm<>(algorithm);
        }

        return algorithm;
    }

    private static <T extends Comparable<? super T>> SearchAlgorithm<T> createBase(SearchType searchType) {
        if (searchType == null) {
            throw new IllegalArgumentException("searchType must not be null");
        }

        switch (searchType) {
            case BINARY:
                return new BinarySearchService<>();
            case LINEAR:
                return new LinearSearchService<>();
            default:
                throw new IllegalArgumentException("Unsupported search type: " + searchType);
        }
    }

    static <T extends Comparable<? super T>> SearchAlgorithm<T> binarySearch() {
        return create(SearchType.BINARY);
    }
}
