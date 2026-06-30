final class BinarySearch {
    private BinarySearch() {
    }

    static int binarySearch(int[] sortedArray, int target) {
        return BinarySearcher.search(sortedArray, target).index();
    }

    public static void main(String[] args) {
        BinarySearchDemo.run();
    }
}
