final class BinarySearch {
    static final int NOT_FOUND = -1;

    private BinarySearch() {
    }

    static int indexOf(int[] sortedValues, int target) {
        int low = 0;
        int high = sortedValues.length - 1;

        while (low <= high) {
            int mid = midpoint(low, high);
            int candidate = sortedValues[mid];
            int comparison = Integer.compare(candidate, target);

            if (comparison == 0) {
                return mid;
            }

            if (comparison < 0) {
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
