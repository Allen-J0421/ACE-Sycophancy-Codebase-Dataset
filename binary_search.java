class BinarySearch {
    public static void main(String[] args) {
        SearchAlgorithm<Integer> searchAlgorithm = new BinarySearchService<>();
        Integer[] values = { 2, 3, 4, 10, 40 };
        Integer target = 10;

        int result = searchAlgorithm.search(values, target);
        if (result == -1) {
            System.out.println("Element is not present in array");
        } else {
            System.out.println("Element is present at index " + result);
        }
    }
}
