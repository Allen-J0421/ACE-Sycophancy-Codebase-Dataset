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
        System.out.println("All counting sort checks passed.");
    }

    private static void shouldSortPositiveAndRepeatedValues() {
        int[] input = {2, 5, 3, 0, 2, 3, 0, 3};
        int[] expected = {0, 0, 2, 2, 3, 3, 3, 5};
        assertSorted(expected, CountingSort.sort(input));
    }

    private static void shouldSortNegativeValues() {
        int[] input = {4, -2, 7, 0, -2, 5};
        int[] expected = {-2, -2, 0, 4, 5, 7};
        assertSorted(expected, CountingSort.countSort(input));
    }

    private static void shouldHandleEmptyAndSingleElementArrays() {
        assertSorted(new int[0], CountingSort.sort(new int[0]));
        assertSorted(new int[] {42}, CountingSort.sort(new int[] {42}));
    }

    private static void shouldRejectNullInput() {
        try {
            CountingSort.sort(null);
            throw new AssertionError("Expected NullPointerException for null input");
        } catch (NullPointerException expected) {
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
}
