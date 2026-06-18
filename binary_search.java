/**
 * Generic binary search implementation for sorted arrays.
 * Provides O(log n) search time complexity with iterative approach
 * to avoid stack overflow on large arrays.
 *
 * @param <T> the type of elements in the array (must implement Comparable)
 */
class BinarySearch<T extends Comparable<T>> {
    private boolean trackStats = false;

    /**
     * Enables statistics tracking for search operations.
     *
     * @return this for method chaining
     */
    public BinarySearch<T> withStats() {
        this.trackStats = true;
        return this;
    }

    /**
     * Searches for a target value in a sorted array.
     *
     * @param array the sorted array to search
     * @param target the value to find
     * @return SearchResult containing index if found, otherwise not found
     */
    public SearchResult search(T[] array, T target) {
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
                if (trackStats) {
                    long elapsed = System.nanoTime() - startTime;
                    SearchStats stats = new SearchStats(comparisons, elapsed);
                    return new SearchResultWithStats(SearchResult.found(mid), stats);
                }
                return SearchResult.found(mid);
            } else if (comparison < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        if (trackStats) {
            long elapsed = System.nanoTime() - startTime;
            SearchStats stats = new SearchStats(comparisons, elapsed);
            return new SearchResultWithStats(SearchResult.notFound(), stats);
        }
        return SearchResult.notFound();
    }

    /**
     * Checks if a target value exists in a sorted array.
     *
     * @param array the sorted array to search
     * @param target the value to find
     * @return true if target is found, false otherwise
     */
    public boolean contains(T[] array, T target) {
        return search(array, target).isFound();
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

    public static void main(String[] args) {
        BinarySearch<Integer> searcher = new BinarySearch<Integer>().withStats();
        Integer[] array = {2, 3, 4, 10, 40};
        Integer target = 10;
        SearchResult result = searcher.search(array, target);

        printResult(result);
    }

    private static void printResult(SearchResult result) {
        if (result.isFound()) {
            System.out.println("Element is present at index " + result.getIndex());
        } else {
            System.out.println("Element is not present in array");
        }

        if (result instanceof SearchResultWithStats) {
            SearchStats stats = ((SearchResultWithStats) result).getStats();
            System.out.println("Statistics: " + stats);
        }
    }
}
