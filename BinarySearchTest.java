public final class BinarySearchTest {
    private BinarySearchTest() {
    }

    public static void main(String[] args) {
        SearchCase[] cases = {
                new SearchCase(new int[] {}, 10, BinarySearch.NOT_FOUND),
                new SearchCase(new int[] { 10 }, 10, 0),
                new SearchCase(new int[] { 2, 3, 4, 10, 40 }, 2, 0),
                new SearchCase(new int[] { 2, 3, 4, 10, 40 }, 10, 3),
                new SearchCase(new int[] { 2, 3, 4, 10, 40 }, 40, 4),
                new SearchCase(new int[] { 2, 3, 4, 10, 40 }, 5, BinarySearch.NOT_FOUND),
                new SearchCase(new int[] { -8, -3, 0, 7, 11 }, -3, 1),
        };

        for (SearchCase searchCase : cases) {
            assertSearch(searchCase);
        }

        assertNullInputRejected();
    }

    private static void assertSearch(SearchCase searchCase) {
        int actualIndex = BinarySearch.indexOf(searchCase.sortedValues, searchCase.target);

        if (actualIndex != searchCase.expectedIndex) {
            throw new AssertionError(
                    "Expected index " + searchCase.expectedIndex + " for target " + searchCase.target
                            + ", but got " + actualIndex);
        }
    }

    private static void assertNullInputRejected() {
        try {
            BinarySearch.indexOf(null, 10);
            throw new AssertionError("Expected null input to be rejected");
        } catch (NullPointerException exception) {
            if (!"sortedValues".equals(exception.getMessage())) {
                throw new AssertionError("Expected null input message to name sortedValues");
            }
        }
    }

    private static final class SearchCase {
        private final int[] sortedValues;
        private final int target;
        private final int expectedIndex;

        private SearchCase(int[] sortedValues, int target, int expectedIndex) {
            this.sortedValues = sortedValues;
            this.target = target;
            this.expectedIndex = expectedIndex;
        }
    }
}
