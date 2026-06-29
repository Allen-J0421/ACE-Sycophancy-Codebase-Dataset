final class BinarySearchTest {
    private BinarySearchTest() {
    }

    public static void main(String[] args) {
        assertSearchFindsExistingValues();
        assertSearchReportsMissingValues();
        assertSearchHandlesEmptyArrays();

        System.out.println("All binary search tests passed");
    }

    private static void assertSearchFindsExistingValues() {
        int[] numbers = { 2, 3, 4, 10, 40 };

        assertEquals(0, BinarySearch.binarySearch(numbers, 2));
        assertEquals(3, BinarySearch.binarySearch(numbers, 10));
        assertEquals(4, BinarySearch.binarySearch(numbers, 40));
    }

    private static void assertSearchReportsMissingValues() {
        int[] numbers = { 2, 3, 4, 10, 40 };

        assertEquals(BinarySearch.NOT_FOUND, BinarySearch.binarySearch(numbers, 1));
        assertEquals(BinarySearch.NOT_FOUND, BinarySearch.binarySearch(numbers, 11));
        assertEquals(BinarySearch.NOT_FOUND, BinarySearch.binarySearch(numbers, 100));
    }

    private static void assertSearchHandlesEmptyArrays() {
        assertEquals(BinarySearch.NOT_FOUND, BinarySearch.binarySearch(new int[0], 10));
    }

    private static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected " + expected + " but got " + actual);
        }
    }
}
