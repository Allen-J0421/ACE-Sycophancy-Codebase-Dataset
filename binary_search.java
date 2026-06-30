class BinarySearch {
    public static void main(String[] args) {
        BinarySearchService searchService = new BinarySearchService();
        int[] arr = { 2, 3, 4, 10, 40 };
        int target = 10;

        int result = searchService.search(arr, target);
        if (result == -1) {
            System.out.println("Element is not present in array");
        } else {
            System.out.println("Element is present at index " + result);
        }
    }
}
