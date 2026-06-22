public final class BubbleSortTest {

    private static final int[] UNSORTED_INPUT = { 64, 34, 25, 12, 22, 11, 90 };
    private static final int[] UNSORTED_OUTPUT = { 11, 12, 22, 25, 34, 64, 90 };
    private static final int[] ALREADY_SORTED_INPUT = { 1, 2, 3, 4 };
    private static final int[] EMPTY_INPUT = {};
    private static final int[] SINGLE_INPUT = { 7 };
    private static final int[] SOURCE_INPUT = { 5, 1, 4, 2, 8 };
    private static final int[] SORTED_COPY_OUTPUT = { 1, 2, 4, 5, 8 };

    private BubbleSortTest() {
        // Test utility class.
    }

    public static void main(String[] args) {
        shouldSortCases();
        shouldCreateSortedCopyWithoutMutatingSource();
        shouldRejectNullInput();
        System.out.println("All BubbleSort tests passed.");
    }

    private static void shouldSortCases() {
        assertSortsInPlace(UNSORTED_INPUT, UNSORTED_OUTPUT);
        assertSortsInPlace(ALREADY_SORTED_INPUT, ALREADY_SORTED_INPUT);
        assertSortsInPlace(EMPTY_INPUT, EMPTY_INPUT);
        assertSortsInPlace(SINGLE_INPUT, SINGLE_INPUT);
    }

    private static void shouldCreateSortedCopyWithoutMutatingSource() {
        int[] source = SOURCE_INPUT.clone();
        int[] sorted = BubbleSort.sortedCopy(source);

        assertIntArrayEquals(SOURCE_INPUT, source);
        assertIntArrayEquals(SORTED_COPY_OUTPUT, sorted);
    }

    private static void shouldRejectNullInput() {
        try {
            BubbleSort.sortInPlace(null);
            throw new AssertionError("Expected IllegalArgumentException for null input");
        } catch (IllegalArgumentException expected) {
            // Expected path.
        }
    }

    private static void assertSortsInPlace(int[] input, int[] expected) {
        int[] values = input.clone();

        BubbleSort.sortInPlace(values);

        assertIntArrayEquals(expected, values);
    }

    private static void assertIntArrayEquals(int[] expected, int[] actual) {
        if (expected.length != actual.length) {
            throw new AssertionError("Array length mismatch");
        }

        for (int index = 0; index < expected.length; index++) {
            if (expected[index] != actual[index]) {
                throw new AssertionError("Array mismatch at index " + index);
            }
        }
    }
}
