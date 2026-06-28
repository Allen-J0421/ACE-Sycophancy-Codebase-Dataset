public final class BinarySearchTest {
    private BinarySearchTest() {
    }

    public static void main(String[] args) {
        SearchCase[] cases = {
            new SearchCase(new int[] {}, 1, -1),
            new SearchCase(new int[] {1}, 1, 0),
            new SearchCase(new int[] {1}, 2, -1),
            new SearchCase(new int[] {1, 3, 5, 7}, 1, 0),
            new SearchCase(new int[] {1, 3, 5, 7}, 5, 2),
            new SearchCase(new int[] {1, 3, 5, 7}, 7, 3),
            new SearchCase(new int[] {1, 3, 5, 7}, 6, -1)
        };

        for (SearchCase searchCase : cases) {
            assertIndexOf(searchCase);
            assertCompatibilityMethod(searchCase);
        }

        assertFoundFlag();

        System.out.println("All binary search tests passed.");
    }

    private static void assertIndexOf(SearchCase searchCase) {
        int actual = BinarySearch.indexOf(searchCase.values, searchCase.target);
        assertEquals(searchCase.expectedIndex, actual, "indexOf", searchCase.target);
    }

    private static void assertCompatibilityMethod(SearchCase searchCase) {
        int actual = BinarySearch.binarySearch(searchCase.values, searchCase.target);
        assertEquals(searchCase.expectedIndex, actual, "binarySearch", searchCase.target);
    }

    private static void assertFoundFlag() {
        if (!BinarySearch.isFound(0)) {
            throw new AssertionError("Expected index 0 to be treated as found.");
        }

        if (BinarySearch.isFound(-1)) {
            throw new AssertionError("Expected index -1 to be treated as not found.");
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
