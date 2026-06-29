final class BinarySearchDemo {
    private static final int DEMO_TARGET = 10;
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";

    private BinarySearchDemo() {
    }

    public static void main(String[] args) {
        System.out.println(run());
    }

    static String run() {
        int[] values = { 2, 3, 4, 10, 40 };
        SearchResult result = BinarySearch.search(values, DEMO_TARGET);

        return formatSearchResult(result);
    }

    private static String formatSearchResult(SearchResult result) {
        if (!result.isFound()) {
            return NOT_FOUND_MESSAGE;
        }

        return FOUND_MESSAGE_PREFIX + result.index();
    }
}
