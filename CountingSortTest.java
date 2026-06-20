import java.util.Arrays;

public final class CountingSortTest {

    private CountingSortTest() {
        // Test utility class.
    }

    public static void main(String[] args) {
        shouldSortPositiveAndRepeatedValues();
        shouldSortNegativeValues();
        shouldHandleEmptyAndSingleElementArrays();
        shouldRejectNullInput();
        shouldRejectImpossibleRanges();
        System.out.println("All counting sort checks passed.");
    }

    private static void shouldSortPositiveAndRepeatedValues() {
        int[] input = {2, 5, 3, 0, 2, 3, 0, 3};
        int[] expected = {0, 0, 2, 2, 3, 3, 3, 5};
        int[] snapshot = input.clone();
        assertSortedAndUnchanged(expected, snapshot, input, CountingSort.sort(input));
    }

    private static void shouldSortNegativeValues() {
        int[] input = {4, -2, 7, 0, -2, 5};
        int[] expected = {-2, -2, 0, 4, 5, 7};
        int[] snapshot = input.clone();
        assertSortedAndUnchanged(expected, snapshot, input, CountingSort.sort(input));
    }

    private static void shouldHandleEmptyAndSingleElementArrays() {
        assertSorted(new int[0], CountingSort.sort(new int[0]));

        int[] single = {42};
        int[] snapshot = single.clone();
        assertSortedAndUnchanged(new int[] {42}, snapshot, single, CountingSort.sort(single));
    }

    private static void shouldRejectNullInput() {
        try {
            CountingSort.sort(null);
            throw new AssertionError("Expected NullPointerException for null input");
        } catch (NullPointerException expected) {
            // Expected path.
        }
    }

    private static void shouldRejectImpossibleRanges() {
        try {
            CountingSort.sort(new int[] {Integer.MIN_VALUE, Integer.MAX_VALUE});
            throw new AssertionError("Expected IllegalArgumentException for impossible range");
        } catch (IllegalArgumentException expected) {
            // Expected path.
        }
    }

    private static void assertSorted(int[] expected, int[] actual) {
        if (!Arrays.equals(expected, actual)) {
            throw new AssertionError(
                "Expected " + Arrays.toString(expected) + " but got " + Arrays.toString(actual)
            );
        }
    }

    private static void assertSortedAndUnchanged(int[] expected, int[] snapshot, int[] original, int[] actual) {
        assertSorted(expected, actual);
        if (!Arrays.equals(snapshot, original)) {
            throw new AssertionError("Input array was modified");
        }
    }
}
