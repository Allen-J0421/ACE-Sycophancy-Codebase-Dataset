public final class BinarySearchTest {
    private BinarySearchTest() {
    }

    public static void main(String[] args) {
        assertIndex(0, new int[] {1}, 1);
        assertIndex(BinarySearch.NOT_FOUND, new int[] {1}, 2);
        assertIndex(0, new int[] {1, 3, 5, 7}, 1);
        assertIndex(2, new int[] {1, 3, 5, 7}, 5);
        assertIndex(3, new int[] {1, 3, 5, 7}, 7);
        assertIndex(BinarySearch.NOT_FOUND, new int[] {}, 1);
        assertThrows(NullPointerException.class, () -> BinarySearch.binarySearch(null, 1));
    }

    private static void assertIndex(int expected, int[] values, int target) {
        int actual = BinarySearch.binarySearch(values, target);
        if (actual != expected) {
            throw new AssertionError("expected index " + expected + " but got " + actual);
        }
    }

    private static void assertThrows(Class<? extends Throwable> expected, ThrowingRunnable action) {
        try {
            action.run();
        } catch (Throwable error) {
            if (expected.isInstance(error)) {
                return;
            }

            throw new AssertionError("expected " + expected.getName() + " but got " + error, error);
        }

        throw new AssertionError("expected " + expected.getName() + " but no exception was thrown");
    }

    private interface ThrowingRunnable {
        void run() throws Throwable;
    }
}
