public final class BubbleSortTest {

    private BubbleSortTest() {
        // Test utility class.
    }

    public static void main(String[] args) {
        shouldSortUnorderedInput();
        shouldLeaveSortedInputUnchanged();
        shouldHandleEmptyAndSingleElementArrays();
        shouldCreateSortedCopyWithoutMutatingSource();
        shouldRejectNullInput();
        System.out.println("All BubbleSort tests passed.");
    }

    private static void shouldSortUnorderedInput() {
        int[] values = { 64, 34, 25, 12, 22, 11, 90 };

        BubbleSort.bubbleSort(values);

        assertArrayEquals(new int[] { 11, 12, 22, 25, 34, 64, 90 }, values);
    }

    private static void shouldLeaveSortedInputUnchanged() {
        int[] values = { 1, 2, 3, 4 };

        BubbleSort.bubbleSort(values);

        assertArrayEquals(new int[] { 1, 2, 3, 4 }, values);
    }

    private static void shouldHandleEmptyAndSingleElementArrays() {
        int[] empty = {};
        int[] single = { 7 };

        BubbleSort.bubbleSort(empty);
        BubbleSort.bubbleSort(single);

        assertArrayEquals(new int[] {}, empty);
        assertArrayEquals(new int[] { 7 }, single);
    }

    private static void shouldCreateSortedCopyWithoutMutatingSource() {
        int[] source = { 5, 1, 4, 2, 8 };

        int[] sorted = BubbleSort.sortedCopy(source);

        assertArrayEquals(new int[] { 5, 1, 4, 2, 8 }, source);
        assertArrayEquals(new int[] { 1, 2, 4, 5, 8 }, sorted);
    }

    private static void shouldRejectNullInput() {
        try {
            BubbleSort.bubbleSort(null);
            throw new AssertionError("Expected IllegalArgumentException for null input");
        } catch (IllegalArgumentException expected) {
            // Expected path.
        }
    }

    private static void assertArrayEquals(int[] expected, int[] actual) {
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
