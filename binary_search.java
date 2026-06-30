final class BinarySearch {
    private static final SearchContainer CONTAINER = SearchContainer.defaultContainer();

    private BinarySearch() {
    }

    static int binarySearch(int[] sortedArray, int target) {
        return CONTAINER.searchAlgorithm().search(sortedArray, target).index();
    }

    public static void main(String[] args) {
        BinarySearchDemo.run(CONTAINER);
    }
}
