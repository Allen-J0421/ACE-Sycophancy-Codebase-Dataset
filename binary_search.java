final class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int DEMO_TARGET = 10;
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";

    private BinarySearch() {
    }

    static int binarySearch(int[] values, int target) {
        int left = 0;
        int right = values.length - 1;

        while (left <= right) {
            int middle = midpoint(left, right);
            int comparison = Integer.compare(values[middle], target);

            if (isMatch(comparison)) {
                return middle;
            }

            if (shouldSearchRight(comparison)) {
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

    private static boolean isMatch(int comparison) {
        return comparison == 0;
    }

    private static boolean shouldSearchRight(int comparison) {
        return comparison < 0;
    }

    private static String formatSearchResult(int result) {
        return isFound(result) ? foundMessage(result) : NOT_FOUND_MESSAGE;
    }

    private static boolean isFound(int result) {
        return result != NOT_FOUND;
    }

    private static String foundMessage(int result) {
        return FOUND_MESSAGE_PREFIX + result;
    }

    public static void main(String[] args) {
        runDemo();
    }

    private static void runDemo() {
        int result = binarySearch(demoValues(), DEMO_TARGET);

        printSearchResult(result);
    }

    private static void printSearchResult(int result) {
        System.out.println(formatSearchResult(result));
    }

    private static int[] demoValues() {
        return new int[] { 2, 3, 4, 10, 40 };
    }
}
