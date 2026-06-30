final class SearchAlgorithmFactory {
    private SearchAlgorithmFactory() {
    }

    static <T extends Comparable<? super T>> SearchAlgorithm<T> binarySearch() {
        return new BinarySearchService<>();
    }
}
