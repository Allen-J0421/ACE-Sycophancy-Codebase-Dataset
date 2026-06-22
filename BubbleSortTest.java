public final class BubbleSortTest {

    private static final int[] SOURCE_INPUT = { 5, 1, 4, 2, 8 };
    private static final int[] SORTED_COPY_OUTPUT = { 1, 2, 4, 5, 8 };
    private static final SortCase[] SORT_CASES = {
        new SortCase(new int[] { 64, 34, 25, 12, 22, 11, 90 }, new int[] { 11, 12, 22, 25, 34, 64, 90 }),
        new SortCase(new int[] { 1, 2, 3, 4 }, new int[] { 1, 2, 3, 4 }),
        new SortCase(new int[] {}, new int[] {}),
        new SortCase(new int[] { 7 }, new int[] { 7 })
    };

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
        for (SortCase sortCase : SORT_CASES) {
            assertSortsInPlace(sortCase);
        }
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

    private static void assertSortsInPlace(SortCase sortCase) {
        int[] values = sortCase.input.clone();

        BubbleSort.sortInPlace(values);

        assertIntArrayEquals(sortCase.expected, values);
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

    private static final class SortCase {
        private final int[] input;
        private final int[] expected;

        private SortCase(int[] input, int[] expected) {
            this.input = input;
            this.expected = expected;
        }
    }
}
