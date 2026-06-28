public final class BinarySearchTest {
    private BinarySearchTest() {
    }

    public static void main(String[] args) {
        SearchCase[] cases = {
            new SearchCase(new int[] {}, 1, BinarySearch.NOT_FOUND),
            new SearchCase(new int[] {1}, 1, 0),
            new SearchCase(new int[] {1}, 2, BinarySearch.NOT_FOUND),
            new SearchCase(new int[] {1, 3, 5, 7}, 1, 0),
            new SearchCase(new int[] {1, 3, 5, 7}, 5, 2),
            new SearchCase(new int[] {1, 3, 5, 7}, 7, 3),
            new SearchCase(new int[] {1, 3, 5, 7}, 6, BinarySearch.NOT_FOUND)
        };

        for (SearchCase searchCase : cases) {
            assertSearchMethods(searchCase);
            assertContains(searchCase);
        }

        assertFoundFlag();

        System.out.println("All binary search tests passed.");
    }

    private static void assertSearchMethods(SearchCase searchCase) {
        assertEquals(
            searchCase.expectedIndex,
            BinarySearch.indexOf(searchCase.values, searchCase.target),
            "indexOf",
            searchCase.target
        );
        assertEquals(
            searchCase.expectedIndex,
            BinarySearch.binarySearch(searchCase.values, searchCase.target),
            "binarySearch",
            searchCase.target
        );
    }

    private static void assertContains(SearchCase searchCase) {
        boolean expected = searchCase.expectedIndex != BinarySearch.NOT_FOUND;
        boolean actual = BinarySearch.contains(searchCase.values, searchCase.target);

        if (actual != expected) {
            throw new AssertionError(
                "contains expected " + expected
                    + " for target " + searchCase.target
                    + " but got " + actual
            );
        }
    }

    private static void assertFoundFlag() {
        if (!BinarySearch.isFound(0)) {
            throw new AssertionError("Expected index 0 to be treated as found.");
        }

        if (BinarySearch.isFound(BinarySearch.NOT_FOUND)) {
            throw new AssertionError("Expected the not-found index to be treated as not found.");
        }
    }

    private static void assertEquals(int expected, int actual, String methodName, int target) {
        if (actual != expected) {
            throw new AssertionError(
                methodName + " expected index " + expected
                    + " for target " + target
                    + " but got " + actual
            );
        }
    }

    private static final class SearchCase {
        private final int[] values;
        private final int target;
        private final int expectedIndex;

        private SearchCase(int[] values, int target, int expectedIndex) {
            this.values = values;
            this.target = target;
            this.expectedIndex = expectedIndex;
        }
    }
}
