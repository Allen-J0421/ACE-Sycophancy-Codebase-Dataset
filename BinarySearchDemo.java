final class BinarySearchDemo {
    private BinarySearchDemo() {
    }

    public static void main(String[] args) {
        int[] sortedArray = {2, 3, 4, 10, 40};
        int target = 10;
        int result = BinarySearch.indexOf(sortedArray, target);

        if (result == BinarySearch.NOT_FOUND) {
            System.out.println("Element is not present in array");
        } else {
            System.out.println("Element is present at index " + result);
        }
    }
}
