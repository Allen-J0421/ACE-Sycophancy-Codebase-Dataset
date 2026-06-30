class BinarySearchTest {
    public static void main(String[] args) {
        findsIntegerValue();
        returnsMinusOneWhenIntegerValueIsMissing();
        findsStringValue();

        System.out.println("All BinarySearch tests passed.");
    }

    private static void findsIntegerValue() {
        Integer[] values = {2, 3, 4, 10, 40};

        assertEquals(3, BinarySearchUtils.binarySearch(values, 10));
    }

    private static void returnsMinusOneWhenIntegerValueIsMissing() {
        Integer[] values = {2, 3, 4, 10, 40};

        assertEquals(-1, BinarySearchUtils.binarySearch(values, 5));
    }

    private static void findsStringValue() {
        String[] values = {"alpha", "bravo", "charlie", "delta"};

        assertEquals(2, BinarySearchUtils.binarySearch(values, "charlie"));
    }

    private static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected " + expected + " but got " + actual);
        }
    }
}
