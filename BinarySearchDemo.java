public final class BinarySearchDemo {
    private static final int SAMPLE_TARGET = 10;
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";

    private BinarySearchDemo() {
    }

    public static void main(String[] args) {
        runSampleSearch();
    }

    static void runSampleSearch() {
        int result = BinarySearch.binarySearch(sampleNumbers(), SAMPLE_TARGET);
        printSearchResult(result);
    }

    private static void printSearchResult(int index) {
        System.out.println(formatSearchResult(index));
    }

    private static String formatSearchResult(int index) {
        return index == BinarySearch.NOT_FOUND
                ? NOT_FOUND_MESSAGE
                : FOUND_MESSAGE_PREFIX + index;
    }

    private static int[] sampleNumbers() {
        return new int[] {2, 3, 4, 10, 40};
    }
}
