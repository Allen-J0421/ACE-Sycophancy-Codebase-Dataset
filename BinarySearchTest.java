public final class BinarySearchTest {
    private BinarySearchTest() {
    }

    public static void main(String[] args) {
        findsExistingValue();
        returnsNotFoundForMissingValue();
        returnsNotFoundForEmptyArray();
        rejectsNullArray();
        formatsFoundResult();
        formatsNotFoundResult();
    }

    private static void findsExistingValue() {
        assertEquals(3, BinarySearch.indexOf(new int[] {2, 3, 4, 10, 40}, 10));
    }

    private static void returnsNotFoundForMissingValue() {
        assertEquals(BinarySearch.NOT_FOUND, BinarySearch.indexOf(new int[] {2, 3, 4, 10, 40}, 5));
    }

    private static void returnsNotFoundForEmptyArray() {
        assertEquals(BinarySearch.NOT_FOUND, BinarySearch.indexOf(new int[] {}, 10));
    }

    private static void rejectsNullArray() {
        try {
            BinarySearch.indexOf(null, 10);
        } catch (NullPointerException expected) {
            return;
        }

        throw new AssertionError("Expected NullPointerException");
    }

    private static void formatsFoundResult() {
        assertEquals("Element is present at index 3", SearchResultFormatter.format(3));
    }

    private static void formatsNotFoundResult() {
        assertEquals("Element is not present in array", SearchResultFormatter.format(BinarySearch.NOT_FOUND));
    }

    private static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected " + expected + " but got " + actual);
        }
    }

    private static void assertEquals(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected \"" + expected + "\" but got \"" + actual + "\"");
        }
    }
}
