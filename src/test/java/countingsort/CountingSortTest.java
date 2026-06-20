package countingsort;

import java.util.Arrays;

public final class CountingSortTest {

    private CountingSortTest() {
        // Test utility class.
    }

    public static void main(String[] args) {
        String[] descriptions = {
            "positive and repeated values",
            "negative values",
            "empty array",
            "single element"
        };
        int[][] inputs = {
            {2, 5, 3, 0, 2, 3, 0, 3},
            {4, -2, 7, 0, -2, 5},
            {},
            {42}
        };
        int[][] expected = {
            {0, 0, 2, 2, 3, 3, 3, 5},
            {-2, -2, 0, 4, 5, 7},
            {},
            {42}
        };
        for (int i = 0; i < descriptions.length; i++) {
            assertSortsTo(descriptions[i], inputs[i], expected[i]);
        }
        assertThrows(NullPointerException.class, () -> CountingSort.sort(null));
        assertThrows(
            IllegalArgumentException.class,
            () -> CountingSort.sort(new int[] {Integer.MIN_VALUE, Integer.MAX_VALUE})
        );
        System.out.println("All counting sort checks passed.");
    }

    private static void assertSortsTo(String description, int[] input, int[] expected) {
        int[] snapshot = input.clone();
        int[] actual = CountingSort.sort(input);
        if (!Arrays.equals(expected, actual)) {
            throw new AssertionError(
                description + ": expected " + Arrays.toString(expected) + " but got " + Arrays.toString(actual)
            );
        }
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
