package countingsort;

import java.util.Arrays;

public final class CountingSortTest {

    private CountingSortTest() {
        // Test utility class.
    }

    public static void main(String[] args) {
        SortCase[] cases = {
            new SortCase(
                "positive and repeated values",
                new int[] {2, 5, 3, 0, 2, 3, 0, 3},
                new int[] {0, 0, 2, 2, 3, 3, 3, 5}
            ),
            new SortCase(
                "negative values",
                new int[] {4, -2, 7, 0, -2, 5},
                new int[] {-2, -2, 0, 4, 5, 7}
            ),
            new SortCase("empty array", new int[0], new int[0]),
            new SortCase("single element", new int[] {42}, new int[] {42})
        };
        for (SortCase sortCase : cases) {
            assertSortsTo(sortCase);
        }
        assertThrows(NullPointerException.class, () -> CountingSort.sort(null));
        assertThrows(
            IllegalArgumentException.class,
            () -> CountingSort.sort(new int[] {Integer.MIN_VALUE, Integer.MAX_VALUE})
        );
        System.out.println("All counting sort checks passed.");
    }

    private static void assertSortsTo(SortCase sortCase) {
        int[] snapshot = sortCase.input.clone();
        int[] actual = CountingSort.sort(sortCase.input);
        if (!Arrays.equals(sortCase.expected, actual)) {
            throw new AssertionError(
                sortCase.description + ": expected "
                    + Arrays.toString(sortCase.expected)
                    + " but got "
                    + Arrays.toString(actual)
            );
        }
        if (!Arrays.equals(snapshot, sortCase.input)) {
            throw new AssertionError(sortCase.description + ": input array was modified");
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

    private static final class SortCase {
        private final String description;
        private final int[] input;
        private final int[] expected;

        private SortCase(String description, int[] input, int[] expected) {
            this.description = description;
            this.input = input;
            this.expected = expected;
        }
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Throwable;
    }
}
