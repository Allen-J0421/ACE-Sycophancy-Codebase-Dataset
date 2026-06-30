final class BinarySearch {
    private static final SearchAlgorithm SEARCH_ALGORITHM = new BinarySearcher();

    private BinarySearch() {
    }

    static int binarySearch(int[] sortedArray, int target) {
        return SEARCH_ALGORITHM.search(sortedArray, target).index();
    }

    public static void main(String[] args) {
        BinarySearchDemo.run();
    }
}
