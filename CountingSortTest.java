import java.util.Arrays;

public final class CountingSortTest {

    private CountingSortTest() {
    }

    public static void main(String[] args) {
        assertArrayEquals(
            new int[] {},
            CountingSort.sortedCopy(new int[] {})
        );
        assertArrayEquals(
            new int[] {-4, -1, 0, 0, 2, 2, 3, 3, 3, 5},
            CountingSort.sortedCopy(new int[] {2, 5, 3, 0, 2, 3, 0, 3, -4, -1})
        );

        int[] output = new int[4];
        CountingSort.copyInto(new int[] {3, -1, 3, 0}, output);
        assertArrayEquals(new int[] {-1, 0, 3, 3}, output);

        int[] values = {3, -1, 3, 0};
        CountingSort.sortInPlace(values);
        assertArrayEquals(new int[] {-1, 0, 3, 3}, values);

        assertThrows(
            IllegalArgumentException.class,
            () -> CountingSort.copyInto(new int[] {1, 2}, new int[1])
        );
        assertThrows(
            NullPointerException.class,
            () -> CountingSort.sortedCopy(null)
        );
    }

    private static void assertArrayEquals(int[] expected, int[] actual) {
        if (!Arrays.equals(expected, actual)) {
            throw new AssertionError(
                "Expected " + Arrays.toString(expected)
                    + " but was " + Arrays.toString(actual)
            );
        }
    }

    private static void assertThrows(
        Class<? extends Throwable> expectedType,
        ThrowingRunnable action
    ) {
        try {
            action.run();
        } catch (Throwable throwable) {
            if (expectedType.isInstance(throwable)) {
                return;
            }

            throw new AssertionError(
                "Expected " + expectedType.getSimpleName()
                    + " but caught " + throwable.getClass().getSimpleName(),
                throwable
            );
        }

        throw new AssertionError(
            "Expected " + expectedType.getSimpleName() + " to be thrown."
        );
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run();
    }
}
