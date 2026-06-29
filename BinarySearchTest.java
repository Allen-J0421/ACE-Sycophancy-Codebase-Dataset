final class BinarySearchTest {
    private BinarySearchTest() {
    }

    private static void assertSearchResult(int[] numbers, int target, int expectedIndex) {
        final int actualIndex = BinarySearch.binarySearch(numbers, target);

        if (actualIndex != expectedIndex) {
            throw new AssertionError(
                    "Expected index " + expectedIndex + " for target " + target + ", but found " + actualIndex);
        }
    }

    public static void main(String[] args) {
        assertSearchResult(new int[] { 2, 3, 4, 10, 40 }, 10, 3);
        assertSearchResult(new int[] { 2, 3, 4, 10, 40 }, 2, 0);
        assertSearchResult(new int[] { 2, 3, 4, 10, 40 }, 40, 4);
        assertSearchResult(new int[] { 2, 3, 4, 10, 40 }, 5, -1);
        assertSearchResult(new int[] {}, 10, -1);
    }
}
