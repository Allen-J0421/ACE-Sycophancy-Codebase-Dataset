public final class BinarySearchTest {
    private static final SearchCase[] SEARCH_CASES = {
        new SearchCase("first element", new int[] { 2, 3, 4, 10, 40 }, 2, 0),
        new SearchCase("middle element", new int[] { 2, 3, 4, 10, 40 }, 10, 3),
        new SearchCase("last element", new int[] { 2, 3, 4, 10, 40 }, 40, 4),
        new SearchCase("below range", new int[] { 2, 3, 4, 10, 40 }, 1, BinarySearch.NOT_FOUND),
        new SearchCase("gap in range", new int[] { 2, 3, 4, 10, 40 }, 11, BinarySearch.NOT_FOUND),
        new SearchCase("above range", new int[] { 2, 3, 4, 10, 40 }, 100, BinarySearch.NOT_FOUND),
        new SearchCase("empty array", new int[0], 10, BinarySearch.NOT_FOUND)
    };

    private BinarySearchTest() {
    }

    public static void main(String[] args) {
        for (SearchCase searchCase : SEARCH_CASES) {
            assertSearchResult(searchCase);
        }

        System.out.println("All binary search tests passed");
    }

    private static void assertSearchResult(SearchCase searchCase) {
        int actual = BinarySearch.binarySearch(searchCase.sortedArray, searchCase.target);
        assertEquals(searchCase.expectedIndex, actual, searchCase.name);
    }

    private static void assertEquals(int expected, int actual, String caseName) {
        if (expected != actual) {
            throw new AssertionError(caseName + ": expected " + expected + " but got " + actual);
        }
    }

    private static final class SearchCase {
        private final String name;
        private final int[] sortedArray;
        private final int target;
        private final int expectedIndex;

        private SearchCase(String name, int[] sortedArray, int target, int expectedIndex) {
            this.name = name;
            this.sortedArray = sortedArray;
            this.target = target;
            this.expectedIndex = expectedIndex;
        }
    }
}
