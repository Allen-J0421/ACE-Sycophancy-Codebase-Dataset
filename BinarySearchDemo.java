final class BinarySearchDemo {
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";

    private BinarySearchDemo() {
    }

    static void run() {
        int[] values = createDemoValues();
        int target = 10;
        int result = BinarySearch.binarySearch(values, target);

        System.out.println(formatSearchResult(result));
    }

    private static int[] createDemoValues() {
        return new int[] { 2, 3, 4, 10, 40 };
    }

    private static String formatSearchResult(int result) {
        if (!BinarySearch.isFoundIndex(result)) {
            return NOT_FOUND_MESSAGE;
        }

        return FOUND_MESSAGE_PREFIX + result;
    }
}
