/**
 * Binary search implementation for sorted arrays.
 * Provides O(log n) search time complexity.
 */
class BinarySearch {
    private static final int NOT_FOUND = -1;

    /**
     * Searches for a target value in a sorted integer array.
     *
     * @param array the sorted array to search
     * @param target the value to find
     * @return the index of target if found, NOT_FOUND otherwise
     */
    public static int search(int[] array, int target) {
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
    private static int performSearch(int[] array, int target, int low, int high) {
        if (low > high) {
            return NOT_FOUND;
        }

        int mid = low + (high - low) / 2;
        int midValue = array[mid];

        if (midValue == target) {
            return mid;
        } else if (midValue < target) {
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
    public static boolean contains(int[] array, int target) {
        return search(array, target) != NOT_FOUND;
    }

    public static void main(String[] args) {
        int[] array = {2, 3, 4, 10, 40};
        int target = 10;
        int result = search(array, target);

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
