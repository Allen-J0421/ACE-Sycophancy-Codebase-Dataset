public final class BinarySearchTest {
    private BinarySearchTest() {
    }

    public static void main(final String[] args) {
        runsSearchCases();
        rejectsNullArray();
        formatsFoundResult();
        formatsMissingResult();

        System.out.println("All BinarySearch tests passed");
    }

    private static void runsSearchCases() {
        final SearchCase[] cases = {
            new SearchCase(new int[] { 2, 3, 4, 10, 40 }, 10, 3),
            new SearchCase(new int[] { 2, 3, 4, 10, 40 }, 5, BinarySearch.NOT_FOUND_INDEX),
            new SearchCase(new int[] {}, 10, BinarySearch.NOT_FOUND_INDEX),
            new SearchCase(new int[] { 10 }, 10, 0),
            new SearchCase(new int[] { 10 }, 5, BinarySearch.NOT_FOUND_INDEX),
        };

        for (final SearchCase searchCase : cases) {
            assertSearchCase(searchCase);
        }
    }

    private static void rejectsNullArray() {
        assertThrows(NullPointerException.class, () -> BinarySearch.binarySearch(null, 10));
    }

    private static void assertSearchCase(final SearchCase searchCase) {
        assertEquals(
                searchCase.expectedIndex,
                BinarySearch.binarySearch(searchCase.sortedValues, searchCase.target));
    }

    private static void formatsFoundResult() {
        assertEquals("Element is present at index 3", SearchResultFormatter.format(3));
    }

    private static void formatsMissingResult() {
        assertEquals("Element is not present in array", SearchResultFormatter.format(BinarySearch.NOT_FOUND_INDEX));
    }

    private static void assertEquals(final int expected, final int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected " + expected + " but got " + actual);
        }
    }

    private static void assertEquals(final String expected, final String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected '" + expected + "' but got '" + actual + "'");
        }
    }

    private static void assertThrows(
            final Class<? extends Throwable> expectedType,
            final ThrowingRunnable runnable) {
        try {
            runnable.run();
        } catch (final Throwable throwable) {
            if (expectedType.isInstance(throwable)) {
                return;
            }

            throw new AssertionError("Expected " + expectedType.getName() + " but got "
                    + throwable.getClass().getName(), throwable);
        }

        throw new AssertionError("Expected " + expectedType.getName() + " to be thrown");
    }

    private static final class SearchCase {
        private final int[] sortedValues;
        private final int target;
        private final int expectedIndex;

        private SearchCase(final int[] sortedValues, final int target, final int expectedIndex) {
            this.sortedValues = sortedValues;
            this.target = target;
            this.expectedIndex = expectedIndex;
        }
    }

    private interface ThrowingRunnable {
        void run();
    }
}
