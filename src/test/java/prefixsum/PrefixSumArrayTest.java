package prefixsum;

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
        shouldComputePrefixSumsAsLongArrayForLargeValues();
        shouldParseCommandLineArguments();
        shouldJoinPrefixSumsForDisplay();
        shouldRejectIntOverflow();
        shouldRejectIntOverflowWhenBoxing();
        shouldRejectInvalidCommandLineArguments();
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
        assertArrayEquals(new long[0], PrefixSumArray.prefixSumsAsLongArray(new int[0]));
        if (!PrefixSumArray.prefixSums(new int[0]).isEmpty()) {
            throw new AssertionError("Expected empty list for empty input");
        }
    }

    private static void shouldComputePrefixSumsAsLongArrayForLargeValues() {
        int[] input = {Integer.MAX_VALUE, 1};
        assertArrayEquals(
            new long[] {2_147_483_647L, 2_147_483_648L},
            PrefixSumArray.prefixSumsAsLongArray(input));
    }

    private static void shouldParseCommandLineArguments() {
        int[] parsed = PrefixSumParser.parse(new String[] {"3", "1", "4", "1", "5"});
        assertArrayEquals(new int[] {3, 1, 4, 1, 5}, parsed);
    }

    private static void shouldJoinPrefixSumsForDisplay() {
        String joined = PrefixSumFormatter.join(Arrays.asList(10, 30, 40, 45, 60));
        if (!"10 30 40 45 60".equals(joined)) {
            throw new AssertionError("Unexpected joined prefix sums: " + joined);
        }
    }

    private static void shouldRejectIntOverflow() {
        assertThrows(ArithmeticException.class, new CheckedRunnable() {
            @Override
            public void run() {
                PrefixSumArray.prefixSumsAsArray(new int[] {Integer.MAX_VALUE, 1});
            }
        });
    }

    private static void shouldRejectIntOverflowWhenBoxing() {
        assertThrows(ArithmeticException.class, new CheckedRunnable() {
            @Override
            public void run() {
                PrefixSumArray.prefixSums(new int[] {Integer.MAX_VALUE, 1});
            }
        });
    }

    private static void shouldRejectInvalidCommandLineArguments() {
        assertThrows(IllegalArgumentException.class, new CheckedRunnable() {
            @Override
            public void run() {
                PrefixSumParser.parse(new String[] {"10", "abc"});
            }
        });
    }

    private static void shouldRejectNullInput() {
        assertThrows(IllegalArgumentException.class, new CheckedRunnable() {
            @Override
            public void run() {
                PrefixSumArray.prefixSumsAsArray(null);
            }
        });
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

    private static void assertArrayEquals(long[] expected, long[] actual) {
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

    private static <T extends Throwable> void assertThrows(
        Class<T> expectedType,
        CheckedRunnable action) {
        try {
            action.run();
        } catch (Throwable thrown) {
            if (expectedType.isInstance(thrown)) {
                return;
            }

            throw new AssertionError(
                "Expected " + expectedType.getSimpleName() + " but got " + thrown.getClass().getSimpleName(),
                thrown);
        }

        throw new AssertionError("Expected " + expectedType.getSimpleName() + " to be thrown");
    }

    private interface CheckedRunnable {
        void run();
    }
}
