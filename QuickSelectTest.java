public final class QuickSelectTest {

    private QuickSelectTest() {
        // Utility class.
    }

    public static void main(String[] args) {
        shouldSelectTheKthSmallestElement();
        shouldHandleDuplicateValues();
        shouldHandleAllEqualValues();
        shouldHandleAlreadySortedInput();
        shouldNotMutateInputWhenUsingDefaultVariant();
        shouldMutateInputWhenUsingInPlaceVariant();
        shouldRejectInvalidInputs();

        System.out.println("QuickSelectTest: all checks passed");
    }

    private static void shouldSelectTheKthSmallestElement() {
        assertSelectionEquals(6, new int[] { 10, 4, 5, 8, 6, 11, 26 }, 3);
        assertSelectionEquals(4, new int[] { 10, 4, 5, 8, 6, 11, 26 }, 1);
        assertSelectionEquals(26, new int[] { 10, 4, 5, 8, 6, 11, 26 }, 7);
    }

    private static void shouldHandleDuplicateValues() {
        assertSelectionEquals(3, new int[] { 7, 1, 7, 3, 7, 2 }, 3);
        assertSelectionEquals(7, new int[] { 7, 1, 7, 3, 7, 2 }, 4);
    }

    private static void shouldHandleAllEqualValues() {
        assertSelectionEquals(5, new int[] { 5, 5, 5, 5, 5 }, 1);
        assertSelectionEquals(5, new int[] { 5, 5, 5, 5, 5 }, 3);
        assertSelectionEquals(5, new int[] { 5, 5, 5, 5, 5 }, 5);
    }

    private static void shouldHandleAlreadySortedInput() {
        assertSelectionEquals(1, new int[] { 1, 2, 3, 4, 5, 6 }, 1);
        assertSelectionEquals(4, new int[] { 1, 2, 3, 4, 5, 6 }, 4);
        assertSelectionEquals(6, new int[] { 1, 2, 3, 4, 5, 6 }, 6);
    }

    private static void shouldNotMutateInputWhenUsingDefaultVariant() {
        int[] values = { 9, 2, 5, 1 };
        int[] original = values.clone();

        assertSelectionEquals(2, values, 2);
        assertArrayEquals(original, values);
    }

    private static void shouldMutateInputWhenUsingInPlaceVariant() {
        int[] values = { 9, 2, 5, 1 };
        int[] original = values.clone();

        assertSelectionEquals(2, values, 2, true);

        assertArrayNotEquals(original, values);
    }

    private static void shouldRejectInvalidInputs() {
        assertThrows(IllegalArgumentException.class, () -> QuickSelect.selectKthSmallest(null, 1));
        assertThrows(IllegalArgumentException.class, () -> QuickSelect.selectKthSmallest(new int[0], 1));
        assertThrows(IllegalArgumentException.class, () -> QuickSelect.selectKthSmallest(new int[] { 1 }, 0));
        assertThrows(IllegalArgumentException.class, () -> QuickSelect.selectKthSmallest(new int[] { 1 }, 2));
        assertThrows(IllegalArgumentException.class, () -> QuickSelect.selectKthSmallestInPlace(null, 1));
        assertThrows(IllegalArgumentException.class, () -> QuickSelect.selectKthSmallestInPlace(new int[0], 1));
        assertThrows(IllegalArgumentException.class, () -> QuickSelect.selectKthSmallestInPlace(new int[] { 1 }, 0));
        assertThrows(IllegalArgumentException.class, () -> QuickSelect.selectKthSmallestInPlace(new int[] { 1 }, 2));
    }

    private static void assertSelectionEquals(int expected, int[] values, int k) {
        assertSelectionEquals(expected, values, k, false);
    }

    private static void assertSelectionEquals(int expected, int[] values, int k, boolean inPlace) {
        int actual = inPlace
            ? QuickSelect.selectKthSmallestInPlace(values, k)
            : QuickSelect.selectKthSmallest(values, k);

        if (expected != actual) {
            throw new AssertionError("Expected " + expected + " but got " + actual);
        }
    }

    private static void assertArrayEquals(int[] expected, int[] actual) {
        if (expected.length != actual.length) {
            throw new AssertionError("Array lengths differ");
        }

        for (int i = 0; i < expected.length; i++) {
            if (expected[i] != actual[i]) {
                throw new AssertionError("Array values differ at index " + i);
            }
        }
    }

    private static void assertArrayNotEquals(int[] expected, int[] actual) {
        if (expected.length != actual.length) {
            return;
        }

        for (int i = 0; i < expected.length; i++) {
            if (expected[i] != actual[i]) {
                return;
            }
        }

        throw new AssertionError("Expected arrays to differ");
    }

    private static void assertThrows(Class<? extends Throwable> expectedType, ThrowingRunnable runnable) {
        try {
            runnable.run();
        } catch (Throwable actual) {
            if (expectedType.isInstance(actual)) {
                return;
            }
            throw new AssertionError(
                "Expected " + expectedType.getSimpleName() + " but got " + actual.getClass().getSimpleName(),
                actual);
        }

        throw new AssertionError("Expected " + expectedType.getSimpleName() + " to be thrown");
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run();
    }
}
