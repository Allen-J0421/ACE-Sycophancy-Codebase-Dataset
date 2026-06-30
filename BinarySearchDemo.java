final class BinarySearchDemo {
    private BinarySearchDemo() {
    }

    static void run() {
        int[] sortedArray = { 2, 3, 4, 10, 40 };
        int target = 10;
        SearchResult result = BinarySearcher.search(sortedArray, target);

        System.out.println(SearchResultFormatter.format(result));
    }
}
