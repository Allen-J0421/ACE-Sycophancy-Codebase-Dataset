final class BinarySearchTest {
    private static final int NOT_FOUND = -1;

    private BinarySearchTest() {
    }

    private static int[] sampleNumbers() {
        return new int[] { 2, 3, 4, 10, 40 };
    }

    private static void assertSearchResult(int[] numbers, int target, int expectedIndex) {
        final int actualIndex = BinarySearch.binarySearch(numbers, target);

        if (actualIndex != expectedIndex) {
            throw new AssertionError(
                    "Expected index " + expectedIndex + " for target " + target + ", but found " + actualIndex);
        }
    }

    public static void main(String[] args) {
        assertSearchResult(sampleNumbers(), 10, 3);
        assertSearchResult(sampleNumbers(), 2, 0);
        assertSearchResult(sampleNumbers(), 40, 4);
        assertSearchResult(sampleNumbers(), 5, NOT_FOUND);
        assertSearchResult(new int[] {}, 10, NOT_FOUND);
    }
}
