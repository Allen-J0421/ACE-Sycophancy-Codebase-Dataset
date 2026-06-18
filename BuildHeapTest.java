import java.util.Arrays;

public final class BuildHeapTest {

    private BuildHeapTest() {
        // Test class.
    }

    public static void main(String[] args) {
        testBuildMaxHeap();
        testBuildMaxHeapPrefix();
        testIsMaxHeap();
        testDegenerateInputs();
        testInvalidArguments();

        System.out.println("All heap tests passed");
    }

    private static void testBuildMaxHeap() {
        int[] values = {1, 3, 5, 4, 6, 13, 10, 9, 8, 15, 17};
        int[] expected = {17, 15, 13, 9, 6, 5, 10, 4, 8, 3, 1};

        BuildHeap.buildMaxHeap(values);

        assertArrayEquals(expected, values, "buildMaxHeap should rearrange the full array");
        assertTrue(BuildHeap.isMaxHeap(values), "Result should satisfy the max-heap property");
    }

    private static void testBuildMaxHeapPrefix() {
        int[] values = {4, 10, 3, 5, 1, 99, 100};

        BuildHeap.buildMaxHeap(values, 5);

        assertTrue(BuildHeap.isMaxHeap(values, 5), "Prefix should be a valid max heap");
        assertEquals(99, values[5], "Elements outside the heap prefix must remain untouched");
        assertEquals(100, values[6], "Elements outside the heap prefix must remain untouched");
    }

    private static void testIsMaxHeap() {
        int[] validHeap = {20, 15, 18, 9, 7, 12, 14};
        int[] invalidHeap = {10, 12, 9};

        assertTrue(BuildHeap.isMaxHeap(validHeap), "Valid heap should be recognized");
        assertFalse(BuildHeap.isMaxHeap(invalidHeap), "Invalid heap should be rejected");
    }

    private static void testDegenerateInputs() {
        int[] empty = {};
        int[] single = {42};

        BuildHeap.buildMaxHeap(empty);
        BuildHeap.buildMaxHeap(single);

        assertTrue(BuildHeap.isMaxHeap(empty), "Empty array is a valid heap");
        assertTrue(BuildHeap.isMaxHeap(single), "Single-element array is a valid heap");
        assertEquals(42, single[0], "Single-element array should remain unchanged");
    }

    private static void testInvalidArguments() {
        expectThrows(NullPointerException.class, () -> BuildHeap.buildMaxHeap(null));
        expectThrows(NullPointerException.class, () -> BuildHeap.isMaxHeap(null));
        expectThrows(IllegalArgumentException.class, () -> BuildHeap.buildMaxHeap(new int[] {1, 2}, 3));
        expectThrows(IllegalArgumentException.class, () -> BuildHeap.buildMaxHeap(new int[] {1, 2}, -1));
        expectThrows(IllegalArgumentException.class, () -> BuildHeap.siftDown(new int[] {1, 2}, 2, 2));
    }

    private static void assertArrayEquals(int[] expected, int[] actual, String message) {
        if (!Arrays.equals(expected, actual)) {
            throw new AssertionError(message + ": expected " + Arrays.toString(expected)
                    + " but was " + Arrays.toString(actual));
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertFalse(boolean condition, String message) {
        assertTrue(!condition, message);
    }

    private static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + ": expected " + expected + " but was " + actual);
        }
    }

    private static void expectThrows(Class<? extends Throwable> expectedType, ThrowingRunnable action) {
        try {
            action.run();
        } catch (Throwable thrown) {
            if (expectedType.isInstance(thrown)) {
                return;
            }

            throw new AssertionError(
                    "Expected " + expectedType.getSimpleName() + " but got " + thrown.getClass().getSimpleName(),
                    thrown);
        }

        throw new AssertionError("Expected " + expectedType.getSimpleName() + " to be thrown");
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Throwable;
    }
}
