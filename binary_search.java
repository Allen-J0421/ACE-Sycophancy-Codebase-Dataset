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
            final int comparison = Integer.compare(middleValue, target);

            if (comparison == 0) {
                return middle;
            }

            if (comparison < 0) {
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
        return index == NOT_FOUND ? NOT_FOUND_MESSAGE : FOUND_MESSAGE_PREFIX + index;
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
