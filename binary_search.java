final class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int[] DEMO_VALUES = { 2, 3, 4, 10, 40 };
    private static final int DEMO_TARGET = 10;
    private static final String NOT_PRESENT_MESSAGE = "Element is not present in array";
    private static final String PRESENT_MESSAGE_PREFIX = "Element is present at index ";

    private BinarySearch() {
    }

    static int binarySearch(int[] values, int target) {
        int low = 0;
        int high = values.length - 1;

        while (low <= high) {
            int mid = midpoint(low, high);
            int candidate = values[mid];

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

    private static int midpoint(int low, int high) {
        return low + (high - low) / 2;
    }

    private static String formatSearchResult(int index) {
        if (index == NOT_FOUND) {
            return NOT_PRESENT_MESSAGE;
        }

        return PRESENT_MESSAGE_PREFIX + index;
    }

    public static void main(String[] args) {
        runDemo();
    }

    private static void runDemo() {
        int result = binarySearch(DEMO_VALUES, DEMO_TARGET);
        System.out.println(formatSearchResult(result));
    }
}
