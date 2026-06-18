/**
 * Facade for binary search operations.
 * Delegates to SearchEngine for the core algorithm implementation.
 *
 * @param <T> the type of elements in the array (must implement Comparable)
 */
class BinarySearch<T extends Comparable<T>> {
    private final SearchEngine<T> engine;

    public BinarySearch() {
        this(SearchConfig.defaults());
    }

    public BinarySearch(SearchConfig config) {
        this.engine = new SearchEngine<>(config);
    }

    /**
     * Creates a new BinarySearch with statistics tracking enabled.
     *
     * @return new BinarySearch instance with stats enabled
     */
    public static <T extends Comparable<T>> BinarySearch<T> withStats() {
        return new BinarySearch<>(SearchConfig.builder().withStats().build());
    }

    /**
     * Searches for a target value in a sorted array.
     *
     * @param array the sorted array to search
     * @param target the value to find
     * @return SearchResult containing index if found, otherwise not found
     */
    public SearchResult search(T[] array, T target) {
        return engine.search(array, target);
    }

    /**
     * Finds the first occurrence of target in sorted array.
     *
     * @param array the sorted array to search
     * @param target the value to find
     * @return SearchResult with first occurrence, or not found
     */
    public SearchResult searchFirst(T[] array, T target) {
        return engine.searchFirst(array, target);
    }

    /**
     * Finds the last occurrence of target in sorted array.
     *
     * @param array the sorted array to search
     * @param target the value to find
     * @return SearchResult with last occurrence, or not found
     */
    public SearchResult searchLast(T[] array, T target) {
        return engine.searchLast(array, target);
    }

    /**
     * Checks if a target value exists in a sorted array.
     *
     * @param array the sorted array to search
     * @param target the value to find
     * @return true if target is found, false otherwise
     */
    public boolean contains(T[] array, T target) {
        return engine.contains(array, target);
    }

    /**
     * Searches and returns the index, or throws if not found.
     *
     * @param array the sorted array to search
     * @param target the value to find
     * @return the index of the target
     * @throws IllegalStateException if target is not found
     */
    public int searchOrThrow(T[] array, T target) {
        return search(array, target).getIndexOrThrow();
    }
}
