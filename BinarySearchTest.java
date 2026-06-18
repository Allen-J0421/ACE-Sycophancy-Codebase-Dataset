public final class BinarySearchTest {
    private BinarySearchTest() {
    }

    public static void main(String[] args) {
        findsExistingValues();
        returnsNotFoundForMissingValues();
        handlesEmptyArrays();
        preservesLegacyMethodName();
        rejectsNullInput();
    }

    private static void findsExistingValues() {
        int[] values = {2, 3, 4, 10, 40};

        assertIndex(0, values, 2);
        assertIndex(2, values, 4);
        assertIndex(4, values, 40);
    }

    private static void returnsNotFoundForMissingValues() {
        int[] values = {2, 3, 4, 10, 40};

        assertIndex(BinarySearch.NOT_FOUND, values, 1);
        assertIndex(BinarySearch.NOT_FOUND, values, 5);
        assertIndex(BinarySearch.NOT_FOUND, values, 41);
    }

    private static void handlesEmptyArrays() {
        assertIndex(BinarySearch.NOT_FOUND, new int[0], 10);
    }

    private static void preservesLegacyMethodName() {
        int[] values = {2, 3, 4, 10, 40};

        assertEquals(3, BinarySearch.binarySearch(values, 10));
    }

    private static void rejectsNullInput() {
        try {
            BinarySearch.indexOf(null, 10);
            throw new AssertionError("Expected NullPointerException");
        } catch (NullPointerException expected) {
            assertEquals("values", expected.getMessage());
        }
    }

    private static void assertIndex(int expected, int[] values, int target) {
        assertEquals(expected, BinarySearch.indexOf(values, target));
    }

    private static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected " + expected + " but found " + actual);
        }
    }

    private static void assertEquals(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected " + expected + " but found " + actual);
        }
    }
}
