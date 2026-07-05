final class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int[] DEMO_VALUES = { 2, 3, 4, 10, 40 };
    private static final int DEMO_TARGET = 10;
    private static final String NOT_PRESENT_MESSAGE = "Element is not present in array";
    private static final String PRESENT_MESSAGE_PREFIX = "Element is present at index ";

    private BinarySearch() {
    }

    static int binarySearch(int[] values, int target) {
        int left = 0;
        int right = values.length - 1;

        while (left <= right) {
            int middle = midpoint(left, right);
            int candidate = values[middle];

            if (candidate == target) {
                return middle;
            }

            if (candidate < target) {
                left = middle + 1;
            } else {
                right = middle - 1;
            }
        }

        return NOT_FOUND;
    }

    private static int midpoint(int left, int right) {
        return left + (right - left) / 2;
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
