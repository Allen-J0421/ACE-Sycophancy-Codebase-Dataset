final class BinarySearchDemo {
    private static final int[] SORTED_VALUES = { 2, 3, 4, 10, 40 };
    private static final int TARGET = 10;

    private BinarySearchDemo() {
    }

    static void run() {
        SearchResult result = BinarySearch.search(SORTED_VALUES, TARGET);

        System.out.println(SearchResultFormatter.format(result));
    }
}
