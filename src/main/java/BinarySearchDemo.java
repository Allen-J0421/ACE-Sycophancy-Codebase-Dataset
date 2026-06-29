final class BinarySearchDemo {
    private BinarySearchDemo() {
    }

    public static void main(String[] args) {
        int[] sortedArray = {2, 3, 4, 10, 40};
        int target = 10;
        SearchResult result = BinarySearch.search(sortedArray, target);

        if (!result.found()) {
            System.out.println("Element is not present in array");
        } else {
            System.out.println("Element is present at index " + result.index());
        }
    }
}
