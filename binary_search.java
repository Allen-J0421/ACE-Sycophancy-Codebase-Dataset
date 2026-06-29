final class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int DEMO_TARGET = 10;
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";

    private BinarySearch() {
    }

    static int binarySearch(int[] sortedNumbers, int target) {
        int low = 0;
        int high = sortedNumbers.length - 1;

        while (low <= high) {
            final int middle = midpoint(low, high);
            final int middleValue = sortedNumbers[middle];

            if (middleValue == target) {
                return middle;
            }

            if (middleValue < target) {
                low = middle + 1;
            } else {
                high = middle - 1;
            }
        }

        return NOT_FOUND;
    }

    private static int midpoint(int low, int high) {
        return low + (high - low) / 2;
    }

    private static String formatSearchResult(int index) {
        if (index == NOT_FOUND) {
            return NOT_FOUND_MESSAGE;
        }

        return FOUND_MESSAGE_PREFIX + index;
    }

    private static int[] demoNumbers() {
        return new int[] { 2, 3, 4, 10, 40 };
    }

    public static void main(String[] args) {
        final int result = binarySearch(demoNumbers(), DEMO_TARGET);

        System.out.println(formatSearchResult(result));
    }
}
