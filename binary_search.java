/**
 * Binary search implementation for sorted arrays.
 * Provides O(log n) search time complexity.
 */
class BinarySearch {
    private static final int NOT_FOUND = -1;

    /**
     * Searches for a target value in a sorted array.
     *
     * @param array the sorted array to search
     * @param target the value to find
     * @return the index of target if found, NOT_FOUND otherwise
     */
    public static int search(int[] array, int target) {
        int low = 0;
        int high = array.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int midValue = array[mid];

            if (midValue == target) {
                return mid;
            } else if (midValue < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return NOT_FOUND;
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
