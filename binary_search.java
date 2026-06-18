/**
 * Generic binary search implementation for sorted arrays.
 * Provides O(log n) search time complexity.
 *
 * @param <T> the type of elements in the array (must implement Comparable)
 */
class BinarySearch<T extends Comparable<T>> {

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
        return performSearch(array, target, 0, array.length - 1);
    }

    /**
     * Recursive binary search implementation.
     *
     * @param array the sorted array to search
     * @param target the value to find
     * @param low the lower bound index
     * @param high the upper bound index
     * @return SearchResult containing the search outcome
     */
    private SearchResult performSearch(T[] array, T target, int low, int high) {
        if (low > high) {
            return SearchResult.notFound();
        }

        int mid = low + (high - low) / 2;
        int comparison = array[mid].compareTo(target);

        if (comparison == 0) {
            return SearchResult.found(mid);
        } else if (comparison < 0) {
            return performSearch(array, target, mid + 1, high);
        } else {
            return performSearch(array, target, low, mid - 1);
        }
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
        BinarySearch<Integer> searcher = new BinarySearch<>();
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
    }
}
