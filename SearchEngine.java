/**
 * Core binary search engine.
 * Encapsulates the search algorithm and statistics tracking.
 *
 * @param <T> the type of elements in the array (must implement Comparable)
 */
class SearchEngine<T extends Comparable<T>> {
    private boolean trackStats = false;

    /**
     * Enables statistics tracking.
     *
     * @return this for method chaining
     */
    SearchEngine<T> withStats() {
        this.trackStats = true;
        return this;
    }

    /**
     * Performs binary search on sorted array.
     *
     * @param array the sorted array to search
     * @param target the value to find
     * @return SearchResult with outcome
     */
    SearchResult search(T[] array, T target) {
        if (array == null || array.length == 0) {
            return SearchResult.notFound();
        }

        long startTime = trackStats ? System.nanoTime() : 0;
        int comparisons = 0;
        int low = 0;
        int high = array.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int comparison = array[mid].compareTo(target);
            comparisons++;

            if (comparison == 0) {
                return buildResult(SearchResult.found(mid), startTime, comparisons);
            } else if (comparison < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return buildResult(SearchResult.notFound(), startTime, comparisons);
    }

    /**
     * Finds the first (leftmost) occurrence of target.
     *
     * @param array the sorted array to search
     * @param target the value to find
     * @return SearchResult with outcome
     */
    SearchResult searchFirst(T[] array, T target) {
        if (array == null || array.length == 0) {
            return SearchResult.notFound();
        }

        long startTime = trackStats ? System.nanoTime() : 0;
        int comparisons = 0;
        int low = 0;
        int high = array.length - 1;
        int result = -1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int comparison = array[mid].compareTo(target);
            comparisons++;

            if (comparison == 0) {
                result = mid;
                high = mid - 1;
            } else if (comparison < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        SearchResult searchResult = result != -1
            ? SearchResult.found(result)
            : SearchResult.notFound();
        return buildResult(searchResult, startTime, comparisons);
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

    private SearchResult buildResult(SearchResult result, long startTime, int comparisons) {
        if (!trackStats) {
            return result;
        }
        long elapsed = System.nanoTime() - startTime;
        SearchStats stats = new SearchStats(comparisons, elapsed);
        return new SearchResultWithStats(result, stats);
    }
}
