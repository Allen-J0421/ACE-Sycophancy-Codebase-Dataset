final class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int DEMO_TARGET = 10;
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";

    private BinarySearch() {
    }

    static int binarySearch(final int[] numbers, final int target) {
        if (isEmpty(numbers)) {
            return NOT_FOUND;
        }

        int low = 0;
        int high = numbers.length - 1;

        while (hasSearchWindow(low, high)) {
            final int mid = midpoint(low, high);
            final int comparison = compareAt(numbers, mid, target);

            if (isMatch(comparison)) {
                return mid;
            }

            if (isBeforeTarget(comparison)) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return NOT_FOUND;
    }

    private static boolean isEmpty(final int[] numbers) {
        return numbers.length == 0;
    }

    private static boolean hasSearchWindow(final int low, final int high) {
        return low <= high;
    }

    private static int midpoint(final int low, final int high) {
        return low + (high - low) / 2;
    }

    private static int compareAt(final int[] numbers, final int index, final int target) {
        return Integer.compare(numbers[index], target);
    }

    private static boolean isMatch(final int comparison) {
        return comparison == 0;
    }

    private static boolean isBeforeTarget(final int comparison) {
        return comparison < 0;
    }

    private static String formatSearchResult(final int result) {
        if (!isFound(result)) {
            return NOT_FOUND_MESSAGE;
        }

        return FOUND_MESSAGE_PREFIX + result;
    }

    private static boolean isFound(final int result) {
        return result != NOT_FOUND;
    }

    private static int[] demoNumbers() {
        return new int[] { 2, 3, 4, 10, 40 };
    }

    private static void runDemo() {
        final int result = binarySearch(demoNumbers(), DEMO_TARGET);

        System.out.println(formatSearchResult(result));
    }

    public static void main(String[] args) {
        runDemo();
    }
}
