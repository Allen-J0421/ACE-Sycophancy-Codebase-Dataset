final class BinarySearchTest {
    private BinarySearchTest() {
    }

    public static void main(String[] args) {
        assertIndex(3, new int[] { 2, 3, 4, 10, 40 }, 10);
        assertIndex(BinarySearch.NOT_FOUND_INDEX, new int[] { 2, 3, 4, 10, 40 }, 5);
        assertIndex(0, new int[] { 7 }, 7);
        assertIndex(BinarySearch.NOT_FOUND_INDEX, new int[] {}, 7);
        assertThrowsNullPointer(new ThrowingRunnable() {
            public void run() {
                BinarySearch.binarySearch(null, 7);
            }
        });

        assertSearchResultFound(3, new int[] { 2, 3, 4, 10, 40 }, 10);
        assertSearchResultNotFound(new int[] { 2, 3, 4, 10, 40 }, 5);

        assertEquals(
                "Element is present at index 3",
                SearchResultFormatter.format(SearchResult.foundAt(3)));
        assertEquals(
                "Element is not present in array",
                SearchResultFormatter.format(SearchResult.notFound()));

        System.out.println("All tests passed");
    }

    private static void assertIndex(int expected, int[] sortedValues, int target) {
        assertEquals(expected, BinarySearch.binarySearch(sortedValues, target));
    }

    private static void assertSearchResultFound(int expectedIndex, int[] sortedValues, int target) {
        SearchResult result = BinarySearch.search(sortedValues, target);

        if (!result.isFound()) {
            throw new AssertionError("Expected search result to be found.");
        }

        assertEquals(expectedIndex, result.index());
    }

    private static void assertSearchResultNotFound(int[] sortedValues, int target) {
        SearchResult result = BinarySearch.search(sortedValues, target);

        if (result.isFound()) {
            throw new AssertionError("Expected search result to be not found.");
        }
    }

    private static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected " + expected + " but got " + actual + ".");
        }
    }

    private static void assertEquals(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected \"" + expected + "\" but got \"" + actual + "\".");
        }
    }

    private static void assertThrowsNullPointer(ThrowingRunnable runnable) {
        try {
            runnable.run();
            throw new AssertionError("Expected NullPointerException.");
        } catch (NullPointerException expected) {
            assertEquals("sortedValues", expected.getMessage());
        }
    }

    private interface ThrowingRunnable {
        void run();
    }
}
