final class BinarySearchTest {
    private BinarySearchTest() {
    }

    public static void main(String[] args) {
        testFindsExistingValue();
        testReturnsNotFoundForMissingValue();
        testHandlesEmptyArray();
        testFormatsFoundResult();
        testFormatsMissingResult();
    }

    private static void testFindsExistingValue() {
        int result = BinarySearch.binarySearch(new int[] { 2, 3, 4, 10, 40 }, 10);

        assertEquals(3, result, "finds an existing value");
    }

    private static void testReturnsNotFoundForMissingValue() {
        int result = BinarySearch.binarySearch(new int[] { 2, 3, 4, 10, 40 }, 5);

        assertEquals(BinarySearch.NOT_FOUND, result, "returns the not-found value when a value is missing");
    }

    private static void testHandlesEmptyArray() {
        int result = BinarySearch.binarySearch(new int[] {}, 10);

        assertEquals(BinarySearch.NOT_FOUND, result, "returns the not-found value for an empty array");
    }

    private static void testFormatsFoundResult() {
        String result = BinarySearch.formatSearchResult(3);

        assertEquals("Element is present at index 3", result, "formats a found result");
    }

    private static void testFormatsMissingResult() {
        String result = BinarySearch.formatSearchResult(BinarySearch.NOT_FOUND);

        assertEquals("Element is not present in array", result, "formats a missing result");
    }

    private static void assertEquals(int expected, int actual, String description) {
        if (expected != actual) {
            throw new AssertionError(description + ": expected " + expected + ", got " + actual);
        }
    }

    private static void assertEquals(String expected, String actual, String description) {
        if (!expected.equals(actual)) {
            throw new AssertionError(description + ": expected \"" + expected + "\", got \"" + actual + "\"");
        }
    }
}
