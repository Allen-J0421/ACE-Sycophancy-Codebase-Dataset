import java.util.Arrays;

public final class HeapSortTest {

    private static final SortCase[] FULL_SORT_CASES = {
        sortCase(new int[] {}, new int[] {}),
        sortCase(new int[] {1}, new int[] {1}),
        sortCase(new int[] {9, 4, 3, 8, 10, 2, 5}, new int[] {2, 3, 4, 5, 8, 9, 10}),
        sortCase(new int[] {3, 3, 2, 1, 2}, new int[] {1, 2, 2, 3, 3}),
        sortCase(new int[] {-1, 4, 0, -7, 4}, new int[] {-7, -1, 0, 4, 4}),
        sortCase(new int[] {1, 2, 3, 4, 5}, new int[] {1, 2, 3, 4, 5}),
        sortCase(new int[] {5, 4, 3, 2, 1}, new int[] {1, 2, 3, 4, 5})
    };

    private static final RangeCase[] RANGE_SORT_CASES = {
        rangeCase(new int[] {9, 7, 3, 8, 1, 6}, 1, 4, new int[] {9, 3, 7, 8, 1, 6})
    };

    private static final InvalidRangeCase[] INVALID_RANGE_CASES = {
        invalidRangeCase(new int[] {1, 2, 3}, -1, 2),
        invalidRangeCase(new int[] {1, 2, 3}, 0, 4),
        invalidRangeCase(new int[] {1, 2, 3}, 2, 1)
    };

    private HeapSortTest() {
        // Utility class; do not instantiate.
    }

    public static void main(String[] args) {
        for (SortCase testCase : FULL_SORT_CASES) {
            assertSort(testCase);
        }

        for (RangeCase testCase : RANGE_SORT_CASES) {
            assertRangeSort(testCase);
        }

        assertNullRejected();
        assertInvalidRanges();
        System.out.println("HeapSort tests passed");
    }

    private static void assertSort(SortCase testCase) {
        int[] actual = Arrays.copyOf(testCase.input(), testCase.input().length);
        HeapSort.sort(actual);
        assertArrayEquals(testCase.expected(), actual);
    }

    private static void assertRangeSort(RangeCase testCase) {
        int[] actual = Arrays.copyOf(testCase.input(), testCase.input().length);
        HeapSort.sort(actual, testCase.fromIndex(), testCase.toIndex());
        assertArrayEquals(testCase.expected(), actual);
    }

    private static void assertNullRejected() {
        expectNullPointer(() -> HeapSort.sort(null));
        expectNullPointer(() -> HeapSort.sort(null, 0, 0));
    }

    private static void assertInvalidRanges() {
        for (InvalidRangeCase testCase : INVALID_RANGE_CASES) {
            expectIndexOutOfBounds(
                    () -> HeapSort.sort(testCase.input(), testCase.fromIndex(), testCase.toIndex()));
        }
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

    private static SortCase sortCase(int[] input, int[] expected) {
        return new SortCase(input, expected);
    }

    private static RangeCase rangeCase(int[] input, int fromIndex, int toIndex, int[] expected) {
        return new RangeCase(input, fromIndex, toIndex, expected);
    }

    private static InvalidRangeCase invalidRangeCase(int[] input, int fromIndex, int toIndex) {
        return new InvalidRangeCase(input, fromIndex, toIndex);
    }

    private static final class SortCase {
        private final int[] input;
        private final int[] expected;

        private SortCase(int[] input, int[] expected) {
            this.input = input;
            this.expected = expected;
        }

        private int[] input() {
            return input;
        }

        private int[] expected() {
            return expected;
        }
    }

    private static final class RangeCase {
        private final int[] input;
        private final int fromIndex;
        private final int toIndex;
        private final int[] expected;

        private RangeCase(int[] input, int fromIndex, int toIndex, int[] expected) {
            this.input = input;
            this.fromIndex = fromIndex;
            this.toIndex = toIndex;
            this.expected = expected;
        }

        private int[] input() {
            return input;
        }

        private int fromIndex() {
            return fromIndex;
        }

        private int toIndex() {
            return toIndex;
        }

        private int[] expected() {
            return expected;
        }
    }

    private static final class InvalidRangeCase {
        private final int[] input;
        private final int fromIndex;
        private final int toIndex;

        private InvalidRangeCase(int[] input, int fromIndex, int toIndex) {
            this.input = input;
            this.fromIndex = fromIndex;
            this.toIndex = toIndex;
        }

        private int[] input() {
            return input;
        }

        private int fromIndex() {
            return fromIndex;
        }

        private int toIndex() {
            return toIndex;
        }
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run();
    }
}
