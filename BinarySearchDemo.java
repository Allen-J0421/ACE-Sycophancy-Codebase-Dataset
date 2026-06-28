final class BinarySearchDemo {
    private static final int SAMPLE_TARGET = 10;
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";

    private BinarySearchDemo() {
    }

    static void run() {
        int index = BinarySearch.binarySearch(sampleValues(), SAMPLE_TARGET);

        System.out.println(formatSearchResult(index));
    }

    private static int[] sampleValues() {
        return new int[] { 2, 3, 4, 10, 40 };
    }

    private static String formatSearchResult(int index) {
        if (!BinarySearch.isFound(index)) {
            return NOT_FOUND_MESSAGE;
        }

        return FOUND_MESSAGE_PREFIX + index;
    }
}
