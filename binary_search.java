final class BinarySearch {
    private static final int DEMO_TARGET = 10;
    private static final int NOT_FOUND = -1;
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";

    private BinarySearch() {
    }

    /**
     * Returns the index of target in a sorted ascending array, or -1 when absent.
     */
    static int binarySearch(int[] values, int target) {
        int left = 0;
        int right = values.length - 1;

        while (left <= right) {
            int middle = midpoint(left, right);
            int middleValue = values[middle];

            if (middleValue == target) {
                return middle;
            }

            if (middleValue < target) {
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

    public static void main(String[] args) {
        runDemo();
    }

    private static void runDemo() {
        int index = binarySearch(demoValues(), DEMO_TARGET);

        System.out.println(formatSearchResult(index));
    }

    private static int[] demoValues() {
        return new int[] { 2, 3, 4, 10, 40 };
    }

    private static String formatSearchResult(int index) {
        return index == NOT_FOUND ? NOT_FOUND_MESSAGE : FOUND_MESSAGE_PREFIX + index;
    }
}
