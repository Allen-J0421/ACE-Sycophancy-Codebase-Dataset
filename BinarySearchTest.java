public final class BinarySearchTest {
    private BinarySearchTest() {
    }

    public static void main(final String[] args) {
        findsExistingValue();
        returnsNotFoundForMissingValue();
        handlesEmptyArray();
        handlesSingleElementArray();
        formatsFoundResult();
        formatsMissingResult();

        System.out.println("All BinarySearch tests passed");
    }

    private static void findsExistingValue() {
        assertEquals(3, BinarySearch.binarySearch(new int[] { 2, 3, 4, 10, 40 }, 10));
    }

    private static void returnsNotFoundForMissingValue() {
        assertEquals(BinarySearch.NOT_FOUND, BinarySearch.binarySearch(new int[] { 2, 3, 4, 10, 40 }, 5));
    }

    private static void handlesEmptyArray() {
        assertEquals(BinarySearch.NOT_FOUND, BinarySearch.binarySearch(new int[] {}, 10));
    }

    private static void handlesSingleElementArray() {
        assertEquals(0, BinarySearch.binarySearch(new int[] { 10 }, 10));
        assertEquals(BinarySearch.NOT_FOUND, BinarySearch.binarySearch(new int[] { 10 }, 5));
    }

    private static void formatsFoundResult() {
        assertEquals("Element is present at index 3", SearchResultFormatter.format(3));
    }

    private static void formatsMissingResult() {
        assertEquals("Element is not present in array", SearchResultFormatter.format(BinarySearch.NOT_FOUND));
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
}
