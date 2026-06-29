final class BinarySearch {
    private static final int[] DEMO_VALUES = { 2, 3, 4, 10, 40 };
    private static final int DEMO_TARGET = 10;
    private static final int NOT_FOUND = -1;
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";

    private BinarySearch() {
    }

    static int binarySearch(int[] values, int target) {
        int low = 0;
        int high = values.length - 1;

        while (low <= high) {
            int mid = midpoint(low, high);

            if (values[mid] == target) {
                return mid;
            }

            if (values[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return NOT_FOUND;
    }

    public static void main(String[] args) {
        runDemo();
    }

    private static int midpoint(int low, int high) {
        return low + (high - low) / 2;
    }

    private static void runDemo() {
        int result = binarySearch(DEMO_VALUES, DEMO_TARGET);

        printSearchResult(result);
    }

    private static void printSearchResult(int result) {
        System.out.println(formatSearchResult(result));
    }

    private static String formatSearchResult(int result) {
        if (isFound(result)) {
            return FOUND_MESSAGE_PREFIX + result;
        }

        return NOT_FOUND_MESSAGE;
    }

    private static boolean isFound(int result) {
        return result != NOT_FOUND;
    }
}
