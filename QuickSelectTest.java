public final class QuickSelectTest {

    private QuickSelectTest() {
        // Utility class.
    }

    public static void main(String[] args) {
        shouldFindTheKthSmallestElement();
        shouldHandleDuplicateValues();
        shouldNotMutateInputWhenUsingDefaultVariant();
        shouldMutateInputWhenUsingInPlaceVariant();
        shouldRejectInvalidInputs();

        System.out.println("QuickSelectTest: all checks passed");
    }

    private static void shouldFindTheKthSmallestElement() {
        int[] values = { 10, 4, 5, 8, 6, 11, 26 };

        assertEquals(6, QuickSelect.selectKthSmallest(values, 3));
        assertEquals(4, QuickSelect.selectKthSmallest(values, 1));
        assertEquals(26, QuickSelect.selectKthSmallest(values, 7));
    }

    private static void shouldHandleDuplicateValues() {
        int[] values = { 7, 1, 7, 3, 7, 2 };

        assertEquals(3, QuickSelect.selectKthSmallest(values, 3));
        assertEquals(7, QuickSelect.selectKthSmallest(values, 4));
    }

    private static void shouldNotMutateInputWhenUsingDefaultVariant() {
        int[] values = { 9, 2, 5, 1 };
        int[] original = values.clone();

        assertEquals(2, QuickSelect.selectKthSmallest(values, 2));
        assertArrayEquals(original, values);
    }

    private static void shouldMutateInputWhenUsingInPlaceVariant() {
        int[] values = { 9, 2, 5, 1 };
        int[] original = values.clone();

        assertEquals(2, QuickSelect.selectKthSmallestInPlace(values, 2));

        boolean mutated = false;
        for (int i = 0; i < values.length; i++) {
            if (values[i] != original[i]) {
                mutated = true;
                break;
            }
        }

        if (!mutated) {
            throw new AssertionError("Expected in-place variant to mutate the input array");
        }
    }

    private static void shouldRejectInvalidInputs() {
        assertThrows(IllegalArgumentException.class, () -> QuickSelect.selectKthSmallest(null, 1));
        assertThrows(IllegalArgumentException.class, () -> QuickSelect.selectKthSmallest(new int[0], 1));
        assertThrows(IllegalArgumentException.class, () -> QuickSelect.selectKthSmallest(new int[] { 1 }, 0));
        assertThrows(IllegalArgumentException.class, () -> QuickSelect.selectKthSmallest(new int[] { 1 }, 2));
    }

    private static void assertEquals(int expected, int actual) {
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
