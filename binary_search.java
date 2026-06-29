import java.util.Objects;

final class BinarySearch {
    private static final int NOT_FOUND = -1;

    private BinarySearch() {
    }

    static int indexOf(int[] sortedArray, int target) {
        Objects.requireNonNull(sortedArray, "sortedArray");

        int left = 0;
        int right = sortedArray.length - 1;

        while (left <= right) {
            int middle = left + (right - left) / 2;
            int middleValue = sortedArray[middle];

            if (middleValue == target) {
                return middle;
            }
            if (middleValue < target) {
                left = middle + 1;
            } else {
                right = middle - 1;
            }
        }

        return NOT_FOUND;
    }

    static boolean contains(int[] sortedArray, int target) {
        return indexOf(sortedArray, target) != NOT_FOUND;
    }

    public static void main(String[] args) {
        int[] sortedArray = {2, 3, 4, 10, 40};
        int target = 10;
        int result = indexOf(sortedArray, target);

        if (result == NOT_FOUND) {
            System.out.println("Element is not present in array");
        } else {
            System.out.println("Element is present at index " + result);
        }
    }
}

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

        assertEquals(-1, BinarySearch.indexOf(values, 5));
        assertFalse(BinarySearch.contains(values, 5));
    }

    private static void handlesEmptyArray() {
        assertEquals(-1, BinarySearch.indexOf(new int[0], 10));
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
