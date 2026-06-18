/**
 * Core binary search engine.
 * Encapsulates the search algorithm with strategy pattern for different variants.
 *
 * @param <T> the type of elements in the array (must implement Comparable)
 */
class SearchEngine<T extends Comparable<T>> {
    private final SearchConfig config;

    SearchEngine(SearchConfig config) {
        this.config = config;
    }

    SearchEngine() {
        this(SearchConfig.defaults());
    }

    /**
     * Performs binary search on sorted array.
     *
     * @param array the sorted array to search
     * @param target the value to find
     * @return SearchResult with outcome
     */
    SearchResult search(T[] array, T target) {
        return performSearch(array, target, new StandardSearchStrategy<>(0, array.length - 1));
    }

    /**
     * Finds the first (leftmost) occurrence of target.
     *
     * @param array the sorted array to search
     * @param target the value to find
     * @return SearchResult with outcome
     */
    SearchResult searchFirst(T[] array, T target) {
        return performSearch(array, target, new FirstOccurrenceStrategy<>(0, array.length - 1));
    }

    /**
     * Finds the last (rightmost) occurrence of target.
     *
     * @param array the sorted array to search
     * @param target the value to find
     * @return SearchResult with outcome
     */
    SearchResult searchLast(T[] array, T target) {
        return performSearch(array, target, new LastOccurrenceStrategy<>(0, array.length - 1));
    }

    /**
     * Checks if target exists in array.
     *
     * @param array the sorted array to search
     * @param target the value to find
     * @return true if found, false otherwise
     */
    boolean contains(T[] array, T target) {
        return search(array, target).isFound();
    }

    /**
     * Core search implementation using strategy pattern.
     *
     * @param array the sorted array to search
     * @param target the value to find
     * @param strategy the search strategy to use
     * @return SearchResult with outcome
     */
    private SearchResult performSearch(T[] array, T target, SearchStrategy<T> strategy) {
        if (array == null || array.length == 0) {
            return SearchResult.notFound();
        }

        long startTime = config.isTrackStats() ? System.nanoTime() : 0;
        int comparisons = 0;

        if (strategy instanceof StandardSearchStrategy) {
            StandardSearchStrategy<T> std = (StandardSearchStrategy<T>) strategy;
            while (std.getLow() <= std.getHigh()) {
                int mid = std.getLow() + (std.getHigh() - std.getLow()) / 2;
                int comparison = array[mid].compareTo(target);
                comparisons++;

                if (!std.processBoundary(comparison, mid)) {
                    break;
                }
            }
        } else if (strategy instanceof FirstOccurrenceStrategy) {
            FirstOccurrenceStrategy<T> first = (FirstOccurrenceStrategy<T>) strategy;
            while (first.getLow() <= first.getHigh()) {
                int mid = first.getLow() + (first.getHigh() - first.getLow()) / 2;
                int comparison = array[mid].compareTo(target);
                comparisons++;

                first.processBoundary(comparison, mid);
            }
        } else if (strategy instanceof LastOccurrenceStrategy) {
            LastOccurrenceStrategy<T> last = (LastOccurrenceStrategy<T>) strategy;
            while (last.getLow() <= last.getHigh()) {
                int mid = last.getLow() + (last.getHigh() - last.getLow()) / 2;
                int comparison = array[mid].compareTo(target);
                comparisons++;

                last.processBoundary(comparison, mid);
            }
        }

        int resultIndex = strategy.getResult();
        SearchResult result = resultIndex != -1
            ? SearchResult.found(resultIndex)
            : SearchResult.notFound();

        return buildResult(result, startTime, comparisons);
    }

    private SearchResult buildResult(SearchResult result, long startTime, int comparisons) {
        if (!config.isTrackStats()) {
            return result;
        }
        long elapsed = System.nanoTime() - startTime;
        SearchStats stats = new SearchStats(comparisons, elapsed);
        return new SearchResultWithStats(result, stats);
    }
}
