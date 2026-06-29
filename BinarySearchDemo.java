public final class BinarySearchDemo {
    private BinarySearchDemo() {
    }

    public static void main(final String[] args) {
        final int[] sortedValues = { 2, 3, 4, 10, 40 };
        final int target = 10;
        final int result = BinarySearch.binarySearch(sortedValues, target);

        printSearchResult(result);
    }

    private static void printSearchResult(final int index) {
        System.out.println(formatSearchResult(index));
    }

    private static String formatSearchResult(final int index) {
        if (index == BinarySearch.NOT_FOUND) {
            return "Element is not present in array";
        }

        return "Element is present at index " + index;
    }
}
