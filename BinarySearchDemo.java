public final class BinarySearchDemo {
    private BinarySearchDemo() {
    }

    public static void main(final String[] args) {
        final int[] sortedValues = { 2, 3, 4, 10, 40 };
        final int target = 10;
        final int result = BinarySearch.binarySearch(sortedValues, target);

        System.out.println(SearchResultFormatter.format(result));
    }
}
