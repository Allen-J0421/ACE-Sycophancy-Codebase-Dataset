final class BinarySearch {
    private static final int NOT_FOUND = -1;

    private BinarySearch() {
    }

    static int indexOf(int[] sortedValues, int target) {
        int low = 0;
        int high = sortedValues.length - 1;

        while (low <= high) {
            int mid = midpoint(low, high);
            int candidate = sortedValues[mid];

            if (candidate == target) {
                return mid;
            }

            if (candidate < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return NOT_FOUND;
    }

    static int binarySearch(int[] sortedValues, int target) {
        return indexOf(sortedValues, target);
    }

    static boolean contains(int[] sortedValues, int target) {
        return isFound(indexOf(sortedValues, target));
    }

    static boolean isFound(int index) {
        return index != NOT_FOUND;
    }

    private static int midpoint(int low, int high) {
        return low + (high - low) / 2;
    }

    public static void main(String[] args) {
        BinarySearchDemo.run();
    }
}
