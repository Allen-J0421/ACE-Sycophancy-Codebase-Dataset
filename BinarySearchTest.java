import java.util.Objects;

final class BinarySearchTest {
    private BinarySearchTest() {
    }

    public static void main(String[] args) {
        findsTargetInMiddle();
        findsTargetAtBounds();
        reportsMissingTarget();
        handlesEmptyArray();
        rejectsNullArray();

        System.out.println("All BinarySearch tests passed");
    }

    private static void findsTargetInMiddle() {
        assertEquals(3, BinarySearch.indexOf(new int[] {2, 3, 4, 10, 40}, 10));
    }

    private static void findsTargetAtBounds() {
        int[] values = {2, 3, 4, 10, 40};

        assertEquals(0, BinarySearch.indexOf(values, 2));
        assertEquals(4, BinarySearch.indexOf(values, 40));
    }

    private static void reportsMissingTarget() {
        int[] values = {2, 3, 4, 10, 40};

        assertEquals(BinarySearch.NOT_FOUND, BinarySearch.indexOf(values, 5));
        assertFalse(BinarySearch.contains(values, 5));
    }

    private static void handlesEmptyArray() {
        assertEquals(BinarySearch.NOT_FOUND, BinarySearch.indexOf(new int[0], 10));
    }

    private static void rejectsNullArray() {
        try {
            BinarySearch.indexOf(null, 10);
            throw new AssertionError("Expected NullPointerException");
        } catch (NullPointerException expected) {
            assertEquals("sortedArray", expected.getMessage());
        }
    }

    private static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected " + expected + " but got " + actual);
        }
    }

    private static void assertEquals(String expected, String actual) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError("Expected " + expected + " but got " + actual);
        }
    }

    private static void assertFalse(boolean condition) {
        if (condition) {
            throw new AssertionError("Expected condition to be false");
        }
    }
}
