import java.util.Arrays;
import java.util.List;

public final class PrefixSumArrayTest {

    private PrefixSumArrayTest() {
        // Utility class.
    }

    public static void main(String[] args) {
        shouldComputePrefixSumsForPositiveNumbers();
        shouldHandleNegativeValues();
        shouldHandleEmptyInput();
        shouldRejectNullInput();
        System.out.println("All PrefixSumArray tests passed.");
    }

    private static void shouldComputePrefixSumsForPositiveNumbers() {
        int[] input = {10, 20, 10, 5, 15};
        assertArrayEquals(new int[] {10, 30, 40, 45, 60}, PrefixSumArray.prefixSumsAsArray(input));

        List<Integer> boxed = PrefixSumArray.prefixSums(input);
        if (!boxed.equals(Arrays.asList(10, 30, 40, 45, 60))) {
            throw new AssertionError("Unexpected boxed prefix sums: " + boxed);
        }
    }

    private static void shouldHandleNegativeValues() {
        int[] input = {4, -2, 7, -3};
        assertArrayEquals(new int[] {4, 2, 9, 6}, PrefixSumArray.prefixSumsAsArray(input));
    }

    private static void shouldHandleEmptyInput() {
        assertArrayEquals(new int[0], PrefixSumArray.prefixSumsAsArray(new int[0]));
        if (!PrefixSumArray.prefixSums(new int[0]).isEmpty()) {
            throw new AssertionError("Expected empty list for empty input");
        }
    }

    private static void shouldRejectNullInput() {
        try {
            PrefixSumArray.prefixSumsAsArray(null);
            throw new AssertionError("Expected IllegalArgumentException for null input");
        } catch (IllegalArgumentException expected) {
            // Expected path.
        }
    }

    private static void assertArrayEquals(int[] expected, int[] actual) {
        if (expected.length != actual.length) {
            throw new AssertionError(
                "Length mismatch: expected " + expected.length + ", got " + actual.length);
        }

        for (int i = 0; i < expected.length; i++) {
            if (expected[i] != actual[i]) {
                throw new AssertionError(
                    "Mismatch at index " + i + ": expected " + expected[i] + ", got " + actual[i]);
            }
        }
    }
}
