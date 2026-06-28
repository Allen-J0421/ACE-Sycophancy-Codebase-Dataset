public final class BinarySearchDemo {
    private BinarySearchDemo() {
    }

    public static void main(String[] args) {
        int[] sortedValues = { 2, 3, 4, 10, 40 };
        int target = 10;
        int result = BinarySearch.binarySearch(sortedValues, target);

        System.out.println(formatSearchResult(result));
    }

    private static String formatSearchResult(int result) {
        if (result == BinarySearch.NOT_FOUND) {
            return "Element is not present in array";
        }

        return "Element is present at index " + result;
    }
}
