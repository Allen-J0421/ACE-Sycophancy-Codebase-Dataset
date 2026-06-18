public final class BinarySearchTest {
    private static final int[] SORTED_VALUES = {2, 3, 4, 10, 40};
    private static final int[] EMPTY_VALUES = new int[0];
    private static final int TARGET = 10;

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
        assertIndex(0, SORTED_VALUES, 2);
        assertIndex(2, SORTED_VALUES, 4);
        assertIndex(4, SORTED_VALUES, 40);
    }

    private static void returnsNotFoundForMissingValues() {
        assertIndex(BinarySearch.NOT_FOUND, SORTED_VALUES, 1);
        assertIndex(BinarySearch.NOT_FOUND, SORTED_VALUES, 5);
        assertIndex(BinarySearch.NOT_FOUND, SORTED_VALUES, 41);
    }

    private static void handlesEmptyArrays() {
        assertIndex(BinarySearch.NOT_FOUND, EMPTY_VALUES, TARGET);
    }

    private static void preservesLegacyMethodName() {
        assertEquals(3, BinarySearch.binarySearch(SORTED_VALUES, TARGET));
    }

    private static void rejectsNullInput() {
        try {
            BinarySearch.indexOf(null, TARGET);
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
