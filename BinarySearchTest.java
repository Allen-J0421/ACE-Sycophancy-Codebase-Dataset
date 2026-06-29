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
        assertSearchResult(sampleArray(), 10, 3, "finds an existing value");
    }

    private static void testReturnsNotFoundForMissingValue() {
        assertSearchResult(
                sampleArray(),
                5,
                BinarySearch.NOT_FOUND,
                "returns the not-found value when a value is missing");
    }

    private static void testHandlesEmptyArray() {
        assertSearchResult(
                new int[] {},
                10,
                BinarySearch.NOT_FOUND,
                "returns the not-found value for an empty array");
    }

    private static void testFormatsFoundResult() {
        assertFormattedResult(3, "Element is present at index 3", "formats a found result");
    }

    private static void testFormatsMissingResult() {
        assertFormattedResult(
                BinarySearch.NOT_FOUND,
                "Element is not present in array",
                "formats a missing result");
    }

    private static void assertSearchResult(int[] array, int target, int expected, String description) {
        int result = BinarySearch.binarySearch(array, target);

        assertEquals(expected, result, description);
    }

    private static void assertFormattedResult(int searchResult, String expected, String description) {
        String result = BinarySearch.formatSearchResult(searchResult);

        assertEquals(expected, result, description);
    }

    private static int[] sampleArray() {
        return new int[] { 2, 3, 4, 10, 40 };
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
