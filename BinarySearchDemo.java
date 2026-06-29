final class BinarySearchDemo {
    private static final int SAMPLE_TARGET = 10;

    private BinarySearchDemo() {
    }

    static void run() {
        SearchResult result = BinarySearch.search(sampleValues(), SAMPLE_TARGET);

        printSearchResult(result);
    }

    public static void main(String[] args) {
        run();
    }

    private static void printSearchResult(SearchResult result) {
        System.out.println(SearchResultFormatter.format(result));
    }

    private static int[] sampleValues() {
        return new int[] { 2, 3, 4, 10, 40 };
    }
}
