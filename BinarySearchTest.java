final class BinarySearchTest {
    private static final int[] SAMPLE_VALUES = { 2, 3, 4, 10, 40 };

    private BinarySearchTest() {
    }

    public static void main(String[] args) {
        assertIndex("finds middle value", SAMPLE_VALUES, 10, 3);
        assertIndex("finds first value", SAMPLE_VALUES, 2, 0);
        assertIndex("finds last value", SAMPLE_VALUES, 40, 4);
        assertNotFound("returns not found for empty array", new int[] {}, 10);
        assertNotFound("returns not found for missing value", SAMPLE_VALUES, 5);
        assertFoundValue("finds a duplicate value", new int[] { 1, 2, 2, 2, 3 }, 2);
        assertIsFound("detects found index", 0, true);
        assertIsFound("detects missing index", -1, false);

        System.out.println("All BinarySearch tests passed");
    }

    private static void assertIndex(String name, int[] values, int target, int expected) {
        int actual = BinarySearch.binarySearch(values, target);

        if (actual != expected) {
            fail(name, "expected index " + expected + " but got " + actual);
        }
    }

    private static void assertNotFound(String name, int[] values, int target) {
        int actual = BinarySearch.binarySearch(values, target);

        if (BinarySearch.isFound(actual)) {
            fail(name, "expected not found but got index " + actual);
        }
    }

    private static void assertFoundValue(String name, int[] values, int target) {
        int actual = BinarySearch.binarySearch(values, target);

        if (actual < 0 || actual >= values.length || values[actual] != target) {
            fail(name, "expected to find " + target + " but got index " + actual);
        }
    }

    private static void assertIsFound(String name, int index, boolean expected) {
        boolean actual = BinarySearch.isFound(index);

        if (actual != expected) {
            fail(name, "expected isFound to be " + expected + " but got " + actual);
        }
    }

    private static void fail(String name, String message) {
        throw new AssertionError(name + ": " + message);
    }
}
