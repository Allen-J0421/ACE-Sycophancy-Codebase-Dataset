/**
 * Generic binary search implementation for sorted arrays.
 * Provides O(log n) search time complexity.
 *
 * @param <T> the type of elements in the array (must implement Comparable)
 */
class BinarySearch<T extends Comparable<T>> {
    private static final int NOT_FOUND = -1;

    /**
     * Searches for a target value in a sorted array.
     *
     * @param array the sorted array to search
     * @param target the value to find
     * @return the index of target if found, NOT_FOUND otherwise
     */
    public int search(T[] array, T target) {
        if (array == null || array.length == 0) {
            return NOT_FOUND;
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
     * @return the index of target if found, NOT_FOUND otherwise
     */
    private int performSearch(T[] array, T target, int low, int high) {
        if (low > high) {
            return NOT_FOUND;
        }

        int mid = low + (high - low) / 2;
        int comparison = array[mid].compareTo(target);

        if (comparison == 0) {
            return mid;
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
        return search(array, target) != NOT_FOUND;
    }

    public static void main(String[] args) {
        BinarySearch<Integer> searcher = new BinarySearch<>();
        Integer[] array = {2, 3, 4, 10, 40};
        Integer target = 10;
        int result = searcher.search(array, target);

        printResult(result);
    }

    private static void printResult(int index) {
        if (index == NOT_FOUND) {
            System.out.println("Element is not present in array");
        } else {
            System.out.println("Element is present at index " + index);
        }
    }
}
