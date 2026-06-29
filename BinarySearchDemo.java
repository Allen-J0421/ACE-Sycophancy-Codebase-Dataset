final class BinarySearchDemo {
    private static final int[] SAMPLE_ARRAY = { 2, 3, 4, 10, 40 };
    private static final int SAMPLE_TARGET = 10;

    private BinarySearchDemo() {
    }

    static void run() {
        int result = BinarySearch.binarySearch(SAMPLE_ARRAY, SAMPLE_TARGET);

        System.out.println(SearchResultFormatter.format(result));
    }
}
