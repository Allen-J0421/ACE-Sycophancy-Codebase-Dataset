class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";

    static int binarySearch(final int[] sortedValues, final int target) {
        int low = 0;
        int high = sortedValues.length - 1;

        while (low <= high) {
            final int mid = midpoint(low, high);
            final int comparison = Integer.compare(sortedValues[mid], target);

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

    private static int midpoint(final int low, final int high) {
        return low + (high - low) / 2;
    }

    private static String formatSearchResult(final int result) {
        if (result == NOT_FOUND) {
            return NOT_FOUND_MESSAGE;
        }

        return FOUND_MESSAGE_PREFIX + result;
    }

    private static void printSearchResult(final int result) {
        System.out.println(formatSearchResult(result));
    }

    private static void runDemo() {
        final int[] sortedValues = { 2, 3, 4, 10, 40 };
        final int target = 10;
        final int result = binarySearch(sortedValues, target);

        printSearchResult(result);
    }

    public static void main(String[] args) {
        runDemo();
    }
}
