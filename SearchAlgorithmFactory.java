final class SearchAlgorithmFactory {
    private SearchAlgorithmFactory() {
    }

    static <T extends Comparable<? super T>> SearchAlgorithm<T> create(SearchType searchType) {
        return create(searchType, false);
    }

    static <T extends Comparable<? super T>> SearchAlgorithm<T> create(SearchType searchType, boolean loggingEnabled) {
        return create(searchType, loggingEnabled, new DefaultSearchRequestValidator<T>(), null);
    }

    static <T extends Comparable<? super T>> SearchAlgorithm<T> create(
            SearchType searchType,
            boolean loggingEnabled,
            ResultHandler resultHandler) {
        return create(searchType, loggingEnabled, new DefaultSearchRequestValidator<T>(), resultHandler);
    }

    static <T extends Comparable<? super T>> SearchAlgorithm<T> create(
            SearchType searchType,
            boolean loggingEnabled,
            SearchRequestValidator<T> validator) {
        return create(searchType, loggingEnabled, validator, null);
    }

    static <T extends Comparable<? super T>> SearchAlgorithm<T> create(
            SearchType searchType,
            boolean loggingEnabled,
            SearchRequestValidator<T> validator,
            ResultHandler resultHandler) {
        SearchAlgorithm<T> algorithm = createBase(searchType);
        if (validator != null) {
            algorithm = new ValidatingSearchAlgorithm<>(algorithm, validator);
        }

        if (loggingEnabled) {
            algorithm = new LoggingSearchAlgorithm<>(algorithm);
        }

        if (resultHandler != null) {
            algorithm = new ResultHandlingSearchAlgorithm<>(algorithm, resultHandler);
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
