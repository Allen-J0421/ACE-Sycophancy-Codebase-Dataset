public final class BinarySearchTest {
    private BinarySearchTest() {
    }

    public static void main(String[] args) {
        assertSearch(new int[] {}, 10, BinarySearch.NOT_FOUND);
        assertSearch(new int[] { 10 }, 10, 0);
        assertSearch(new int[] { 2, 3, 4, 10, 40 }, 2, 0);
        assertSearch(new int[] { 2, 3, 4, 10, 40 }, 10, 3);
        assertSearch(new int[] { 2, 3, 4, 10, 40 }, 40, 4);
        assertSearch(new int[] { 2, 3, 4, 10, 40 }, 5, BinarySearch.NOT_FOUND);
        assertSearch(new int[] { -8, -3, 0, 7, 11 }, -3, 1);
        assertNullInputRejected();
    }

    private static void assertSearch(int[] sortedValues, int target, int expectedIndex) {
        int actualIndex = BinarySearch.binarySearch(sortedValues, target);

        if (actualIndex != expectedIndex) {
            throw new AssertionError(
                    "Expected index " + expectedIndex + " for target " + target + ", but got " + actualIndex);
        }
    }

    private static void assertNullInputRejected() {
        try {
            BinarySearch.binarySearch(null, 10);
            throw new AssertionError("Expected null input to be rejected");
        } catch (NullPointerException exception) {
            if (!"sortedValues".equals(exception.getMessage())) {
                throw new AssertionError("Expected null input message to name sortedValues");
            }
        }
    }
}
