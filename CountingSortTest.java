import java.util.Arrays;

public final class CountingSortTest {

    private CountingSortTest() {
        // Test utility class.
    }

    public static void main(String[] args) {
        assertSortsTo(
            "positive and repeated values",
            new int[] {2, 5, 3, 0, 2, 3, 0, 3},
            new int[] {0, 0, 2, 2, 3, 3, 3, 5}
        );
        assertSortsTo(
            "negative values",
            new int[] {4, -2, 7, 0, -2, 5},
            new int[] {-2, -2, 0, 4, 5, 7}
        );
        assertSortsTo("empty array", new int[0], new int[0]);
        assertSortsTo("single element", new int[] {42}, new int[] {42});
        assertThrows(NullPointerException.class, () -> CountingSort.sort(null));
        assertThrows(
            IllegalArgumentException.class,
            () -> CountingSort.sort(new int[] {Integer.MIN_VALUE, Integer.MAX_VALUE})
        );
        System.out.println("All counting sort checks passed.");
    }

    private static void assertSorted(int[] expected, int[] actual) {
        if (!Arrays.equals(expected, actual)) {
            throw new AssertionError(
                "Expected " + Arrays.toString(expected) + " but got " + Arrays.toString(actual)
            );
        }
    }

    private static void assertSortsTo(String description, int[] input, int[] expected) {
        int[] snapshot = input.clone();
        int[] actual = CountingSort.sort(input);
        assertSorted(expected, actual);
        if (!Arrays.equals(snapshot, input)) {
            throw new AssertionError(description + ": input array was modified");
        }
    }

    private static <T extends Throwable> void assertThrows(Class<T> expectedType, ThrowingRunnable action) {
        try {
            action.run();
            throw new AssertionError("Expected " + expectedType.getSimpleName() + " to be thrown");
        } catch (Throwable thrown) {
            if (!expectedType.isInstance(thrown)) {
                throw new AssertionError(
                    "Expected " + expectedType.getSimpleName() + " but got " + thrown.getClass().getSimpleName(),
                    thrown
                );
            }
        }
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Throwable;
    }
}
