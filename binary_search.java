class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int DEMO_TARGET = 10;
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";

    static int binarySearch(final int[] sortedValues, final int target) {
        int lowerBound = 0;
        int upperBound = lastIndex(sortedValues);

        while (lowerBound <= upperBound) {
            final int mid = midpoint(lowerBound, upperBound);
            final int comparison = Integer.compare(sortedValues[mid], target);

            if (comparison == 0) {
                return mid;
            }

            if (comparison < 0) {
                lowerBound = mid + 1;
            } else {
                upperBound = mid - 1;
            }
        }

        return NOT_FOUND;
    }

    private static int lastIndex(final int[] values) {
        return values.length - 1;
    }

    private static int midpoint(final int lowerBound, final int upperBound) {
        return lowerBound + (upperBound - lowerBound) / 2;
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

    private static int[] demoSortedValues() {
        return new int[] { 2, 3, 4, 10, 40 };
    }

    private static void runDemo() {
        final int result = binarySearch(demoSortedValues(), DEMO_TARGET);

        printSearchResult(result);
    }

    public static void main(String[] args) {
        runDemo();
    }
}
