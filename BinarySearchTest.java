final class BinarySearchTest {
    private static final SearchCase[] SEARCH_CASES = {
            new SearchCase(3, new int[] { 2, 3, 4, 10, 40 }, 10),
            new SearchCase(BinarySearch.NOT_FOUND_INDEX, new int[] { 2, 3, 4, 10, 40 }, 5),
            new SearchCase(0, new int[] { 7 }, 7),
            new SearchCase(BinarySearch.NOT_FOUND_INDEX, new int[] {}, 7)
    };

    private BinarySearchTest() {
    }

    public static void main(String[] args) {
        assertSearchCases();
        assertThrowsNullPointer(() -> BinarySearch.binarySearch(null, 7));

        assertSearchResultFromIndex();

        assertEquals(
                "Element is present at index 3",
                SearchResultFormatter.format(SearchResult.foundAt(3)));
        assertEquals(
                "Element is not present in array",
                SearchResultFormatter.format(SearchResult.notFound()));

        System.out.println("All tests passed");
    }

    private static void assertSearchCases() {
        for (SearchCase searchCase : SEARCH_CASES) {
            int actualIndex = BinarySearch.binarySearch(searchCase.sortedValues, searchCase.target);
            SearchResult result = BinarySearch.search(searchCase.sortedValues, searchCase.target);

            assertEquals(searchCase.expectedIndex, actualIndex);
            assertSearchResult(searchCase.expectedIndex, result);
        }
    }

    private static void assertSearchResultFromIndex() {
        assertSearchResult(3, SearchResult.fromIndex(3));
        assertSearchResult(BinarySearch.NOT_FOUND_INDEX, SearchResult.fromIndex(BinarySearch.NOT_FOUND_INDEX));
    }

    private static void assertSearchResult(int expectedIndex, SearchResult result) {
        if (expectedIndex == BinarySearch.NOT_FOUND_INDEX) {
            if (result.isFound()) {
                throw new AssertionError("Expected search result to be not found.");
            }
            return;
        }

        if (!result.isFound()) {
            throw new AssertionError("Expected search result to be found.");
        }

        assertEquals(expectedIndex, result.index());
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

    private static final class SearchCase {
        private final int expectedIndex;
        private final int[] sortedValues;
        private final int target;

        private SearchCase(int expectedIndex, int[] sortedValues, int target) {
            this.expectedIndex = expectedIndex;
            this.sortedValues = sortedValues;
            this.target = target;
        }
    }
}
