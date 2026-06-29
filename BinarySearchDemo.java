public final class BinarySearchDemo {
    private static final int[] SAMPLE_VALUES = { 2, 3, 4, 10, 40 };
    private static final int SAMPLE_TARGET = 10;
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";

    private BinarySearchDemo() {
    }

    public static void main(String[] args) {
        int result = BinarySearch.binarySearch(SAMPLE_VALUES, SAMPLE_TARGET);

        printSearchResult(result);
    }

    private static String formatSearchResult(int result) {
        if (isNotFound(result)) {
            return NOT_FOUND_MESSAGE;
        }

        return FOUND_MESSAGE_PREFIX + result;
    }

    private static boolean isNotFound(int result) {
        return !BinarySearch.isFound(result);
    }

    private static void printSearchResult(int result) {
        System.out.println(formatSearchResult(result));
    }
}
