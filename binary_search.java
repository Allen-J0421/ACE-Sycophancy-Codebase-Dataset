final class BinarySearch {
    private static final int NOT_FOUND = -1;

    private BinarySearch() {
    }

    static int binarySearch(int[] values, int target) {
        int low = 0;
        int high = values.length - 1;

        while (low <= high) {
            int mid = midpoint(low, high);
            int current = values[mid];

            if (current == target) {
                return mid;
            }

            if (current < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return NOT_FOUND;
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

final class BinarySearchDemo {
    private static final int SAMPLE_TARGET = 10;
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";

    private BinarySearchDemo() {
    }

    static void run() {
        int index = BinarySearch.binarySearch(sampleValues(), SAMPLE_TARGET);

        System.out.println(formatSearchResult(index));
    }

    private static int[] sampleValues() {
        return new int[] { 2, 3, 4, 10, 40 };
    }

    private static String formatSearchResult(int index) {
        if (!BinarySearch.isFound(index)) {
            return NOT_FOUND_MESSAGE;
        }

        return FOUND_MESSAGE_PREFIX + index;
    }
}
