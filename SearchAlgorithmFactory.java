final class SearchAlgorithmFactory {
    private SearchAlgorithmFactory() {
    }

    static <T extends Comparable<? super T>> SearchAlgorithm<T> create(SearchType searchType) {
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
