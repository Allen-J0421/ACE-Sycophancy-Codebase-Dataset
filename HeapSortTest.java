import java.util.Arrays;

public final class HeapSortTest {

    private static final int[] NORMAL_INPUT = {9, 4, 3, 8, 10, 2, 5};
    private static final int[] NORMAL_OUTPUT = {2, 3, 4, 5, 8, 9, 10};
    private static final int[] DUPLICATE_INPUT = {3, 3, 2, 1, 2};
    private static final int[] DUPLICATE_OUTPUT = {1, 2, 2, 3, 3};
    private static final int[] NEGATIVE_INPUT = {-1, 4, 0, -7, 4};
    private static final int[] NEGATIVE_OUTPUT = {-7, -1, 0, 4, 4};
    private static final int[] RANGE_INPUT = {9, 7, 3, 8, 1, 6};
    private static final int[] RANGE_OUTPUT = {9, 3, 7, 8, 1, 6};

    private HeapSortTest() {
        // Utility class; do not instantiate.
    }

    public static void main(String[] args) {
        assertSort(new int[] {}, new int[] {});
        assertSort(new int[] {1}, new int[] {1});
        assertSort(NORMAL_INPUT, NORMAL_OUTPUT);
        assertSort(DUPLICATE_INPUT, DUPLICATE_OUTPUT);
        assertSort(NEGATIVE_INPUT, NEGATIVE_OUTPUT);
        assertSort(new int[] {1, 2, 3, 4, 5}, new int[] {1, 2, 3, 4, 5});
        assertSort(new int[] {5, 4, 3, 2, 1}, new int[] {1, 2, 3, 4, 5});
        assertRangeSort(RANGE_INPUT, 1, 4, RANGE_OUTPUT);
        assertNullRejected();
        assertInvalidRanges();
        System.out.println("HeapSort tests passed");
    }

    private static void assertSort(int[] input, int[] expected) {
        int[] actual = Arrays.copyOf(input, input.length);
        HeapSort.sort(actual);
        assertArrayEquals(expected, actual);
    }

    private static void assertRangeSort(int[] input, int fromIndex, int toIndex, int[] expected) {
        int[] actual = Arrays.copyOf(input, input.length);
        HeapSort.sort(actual, fromIndex, toIndex);
        assertArrayEquals(expected, actual);
    }

    private static void assertNullRejected() {
        expectNullPointer(() -> HeapSort.sort(null));
        expectNullPointer(() -> HeapSort.sort(null, 0, 0));
    }

    private static void assertInvalidRanges() {
        expectIndexOutOfBounds(() -> HeapSort.sort(new int[] {1, 2, 3}, -1, 2));
        expectIndexOutOfBounds(() -> HeapSort.sort(new int[] {1, 2, 3}, 0, 4));
        expectIndexOutOfBounds(() -> HeapSort.sort(new int[] {1, 2, 3}, 2, 1));
    }

    private static void assertArrayEquals(int[] expected, int[] actual) {
        if (!Arrays.equals(expected, actual)) {
            throw new AssertionError(
                    "Expected " + Arrays.toString(expected) + " but got " + Arrays.toString(actual));
        }
    }

    private static void expectNullPointer(ThrowingRunnable action) {
        expectException(action, NullPointerException.class, "Expected NullPointerException");
    }

    private static void expectIndexOutOfBounds(ThrowingRunnable action) {
        expectException(action, IndexOutOfBoundsException.class, "Expected IndexOutOfBoundsException");
    }

    private static void expectException(
            ThrowingRunnable action, Class<? extends RuntimeException> expectedType, String failureMessage) {
        try {
            action.run();
        } catch (RuntimeException exception) {
            if (expectedType.isInstance(exception)) {
                return;
            }

            throw new AssertionError(
                    failureMessage + ", but got " + exception.getClass().getSimpleName(), exception);
        }

        throw new AssertionError(failureMessage);
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run();
    }
}
