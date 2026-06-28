public final class BinarySearch {
    public static final int NOT_FOUND = -1;

    private BinarySearch() {
        // Utility class.
    }

    public static int binarySearch(int[] sortedValues, int target) {
        return indexOf(sortedValues, target);
    }

    public static void main(String[] args) {
        BinarySearchDemo.main(args);
    }

    public static int indexOf(int[] sortedValues, int target) {
        int low = 0;
        int high = sortedValues.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int midValue = sortedValues[mid];

            if (midValue == target) {
                return mid;
            }

            if (midValue < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return NOT_FOUND;
    }
}
