public class ActivitySelectionTest {
    public static void main(String[] args) {
        assertEquals(
            4,
            ActivitySelection.activitySelection(
                new int[] {1, 3, 0, 5, 8, 5},
                new int[] {2, 4, 6, 7, 9, 9}
            ),
            "selects the expected number of compatible activities"
        );

        assertEquals(
            0,
            ActivitySelection.activitySelection(new int[] {}, new int[] {}),
            "returns zero for empty input"
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> ActivitySelection.activitySelection(new int[] {1, 2}, new int[] {3}),
            "rejects mismatched input lengths"
        );

        assertThrows(
            NullPointerException.class,
            () -> ActivitySelection.activitySelection(null, new int[] {}),
            "rejects null start input"
        );

        assertEquals(
            1,
            ActivitySelection.activitySelection(new int[] {1, 2}, new int[] {2, 3}),
            "preserves the strict compatibility rule when activities touch"
        );
    }

    private static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + ": expected " + expected + " but got " + actual);
        }
    }

    private static void assertThrows(
        Class<? extends Throwable> expected,
        ThrowingRunnable runnable,
        String message
    ) {
        try {
            runnable.run();
        } catch (Throwable actual) {
            if (expected.isInstance(actual)) {
                return;
            }
            throw new AssertionError(
                message + ": expected " + expected.getSimpleName() + " but got " + actual.getClass().getSimpleName(),
                actual
            );
        }

        throw new AssertionError(message + ": expected " + expected.getSimpleName() + " to be thrown");
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Throwable;
    }
}
