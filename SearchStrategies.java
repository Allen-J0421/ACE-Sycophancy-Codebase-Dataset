final class SearchStrategies {
    private SearchStrategies() {
    }

    static <T> SearchStrategy<T> iterativeBinarySearch() {
        return new IterativeBinarySearchStrategy<T>();
    }

    static <T> SearchStrategy<T> recursiveBinarySearch() {
        return new RecursiveBinarySearchStrategy<T>();
    }
}
