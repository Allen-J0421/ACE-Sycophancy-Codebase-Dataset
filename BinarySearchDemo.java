final class BinarySearchDemo {
    private BinarySearchDemo() {
    }

    static void run() {
        int[] values = {2, 3, 4, 10, 40};
        int target = 10;
        int result = BinarySearch.indexOf(values, target);

        if (BinarySearch.isFound(result)) {
            System.out.println("Element is present at index " + result);
        } else {
            System.out.println("Element is not present in array");
        }
    }
}
