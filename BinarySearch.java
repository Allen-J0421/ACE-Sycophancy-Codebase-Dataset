public final class BinarySearch {
    private static final int NOT_FOUND = -1;

    private BinarySearch() {
    }

    public static int binarySearch(int[] array, int target) {
        return indexOf(array, target);
    }

    public static int indexOf(int[] array, int target) {
        int low = 0;
        int high = array.length - 1;

        while (low <= high) {
            int mid = midpoint(low, high);
            int value = array[mid];

            if (value == target) {
                return mid;
            }

            if (value < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return NOT_FOUND;
    }

    public static boolean isFound(int result) {
        return result != NOT_FOUND;
    }

    private static int midpoint(int low, int high) {
        return low + (high - low) / 2;
    }

    public static void main(String[] args) {
        BinarySearchDemo.main(args);
    }
}
