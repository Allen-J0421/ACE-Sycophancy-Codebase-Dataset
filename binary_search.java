final class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int[] DEMO_NUMBERS = { 2, 3, 4, 10, 40 };
    private static final int DEMO_TARGET = 10;
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";

    private BinarySearch() {
    }

    static int binarySearch(final int[] numbers, final int target) {
        int low = 0;
        int high = numbers.length - 1;

        while (low <= high) {
            final int mid = midpoint(low, high);

            if (numbers[mid] == target) {
                return mid;
            }

            if (numbers[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return NOT_FOUND;
    }

    private static int midpoint(final int low, final int high) {
        return low + (high - low) / 2;
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

    public static void main(String[] args) {
        final int result = binarySearch(DEMO_NUMBERS, DEMO_TARGET);

        System.out.println(formatSearchResult(result));
    }
}
