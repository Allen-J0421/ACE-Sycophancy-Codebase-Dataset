public final class BinarySearchDemo {
    private static final int SAMPLE_TARGET = 10;
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";

    private BinarySearchDemo() {
    }

    public static void main(String[] args) {
        runDemo();
    }

    private static void runDemo() {
        int result = BinarySearch.binarySearch(sampleValues(), SAMPLE_TARGET);
        System.out.println(formatResult(result));
    }

    private static int[] sampleValues() {
        return new int[] { 2, 3, 4, 10, 40 };
    }

    private static String formatResult(int searchResult) {
        if (searchResult == BinarySearch.NOT_FOUND) {
            return NOT_FOUND_MESSAGE;
        }

        return FOUND_MESSAGE_PREFIX + searchResult;
    }
}
