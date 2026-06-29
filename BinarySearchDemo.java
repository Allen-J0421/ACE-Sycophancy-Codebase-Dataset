final class BinarySearchDemo {
    private static final int SAMPLE_TARGET = 10;

    private BinarySearchDemo() {
    }

    static void run() {
        int result = BinarySearch.binarySearch(sampleValues(), SAMPLE_TARGET);

        printSearchResult(result);
    }

    public static void main(String[] args) {
        run();
    }

    private static void printSearchResult(int index) {
        System.out.println(SearchResultFormatter.format(index));
    }

    private static int[] sampleValues() {
        return new int[] { 2, 3, 4, 10, 40 };
    }
}
