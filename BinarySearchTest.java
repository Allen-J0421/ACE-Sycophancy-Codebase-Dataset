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
        int[] sortedValues = {2, 3, 4, 10, 40};

        assertIndex(0, sortedValues, 2);
        assertIndex(2, sortedValues, 4);
        assertIndex(4, sortedValues, 40);
    }

    private static void returnsNotFoundForMissingValues() {
        int[] sortedValues = {2, 3, 4, 10, 40};

        assertIndex(BinarySearch.NOT_FOUND, sortedValues, 1);
        assertIndex(BinarySearch.NOT_FOUND, sortedValues, 5);
        assertIndex(BinarySearch.NOT_FOUND, sortedValues, 41);
    }

    private static void handlesEmptyArrays() {
        assertIndex(BinarySearch.NOT_FOUND, new int[0], 10);
    }

    private static void preservesLegacyMethodName() {
        int[] sortedValues = {2, 3, 4, 10, 40};

        assertEquals(3, BinarySearch.binarySearch(sortedValues, 10));
    }

    private static void rejectsNullInput() {
        try {
            BinarySearch.indexOf(null, 10);
            throw new AssertionError("Expected NullPointerException");
        } catch (NullPointerException expected) {
            assertEquals("sortedValues", expected.getMessage());
        }
    }

    private static void assertIndex(int expected, int[] sortedValues, int target) {
        assertEquals(expected, BinarySearch.indexOf(sortedValues, target));
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
