public final class TwoPointersTest {

    private TwoPointersTest() {
    }

    public static void main(String[] args) {
        shouldFindMatchingPair();
        shouldReturnEmptyWhenNoPairExists();
        shouldRejectUnsortedInput();
        shouldHandleIntegerOverflowSafely();
        shouldSupportTargetsBeyondIntegerRange();
        shouldDefensivelyCopyValidatedInput();
    }

    private static void shouldFindMatchingPair() {
        SortedIntArray sortedValues = SortedIntArray.copyOf(new int[] {-3, -1, 0, 1, 2});
        TwoPointers.PairMatch match = TwoPointers.findPairWithSum(
            sortedValues,
            -2
        ).orElseThrow(() -> new AssertionError("Expected a matching pair"));

        assertEquals(0, match.leftIndex(), "left index");
        assertEquals(3, match.rightIndex(), "right index");
        assertEquals(-3, match.leftValue(), "left value");
        assertEquals(1, match.rightValue(), "right value");
        assertEquals(-2, match.sum(), "pair sum");
    }

    private static void shouldReturnEmptyWhenNoPairExists() {
        if (TwoPointers.findPairWithSum(SortedIntArray.copyOf(new int[] {1, 4, 8}), 6).isPresent()) {
            throw new AssertionError("Expected no matching pair");
        }
    }

    private static void shouldRejectUnsortedInput() {
        try {
            SortedIntArray.copyOf(new int[] {3, 1, 2});
            throw new AssertionError("Expected unsorted input to be rejected");
        } catch (IllegalArgumentException expected) {
            if (!expected.getMessage().contains("sorted")) {
                throw new AssertionError("Unexpected validation message: " + expected.getMessage());
            }
        }
    }

    private static void shouldHandleIntegerOverflowSafely() {
        TwoPointers.PairMatch match = TwoPointers.findPairWithSum(
            SortedIntArray.copyOf(new int[] {-1, Integer.MAX_VALUE}),
            Integer.MAX_VALUE - 1
        ).orElseThrow(() -> new AssertionError("Expected overflow-safe matching pair"));

        assertEquals(Integer.MAX_VALUE - 1L, match.sum(), "overflow-safe sum");
    }

    private static void shouldSupportTargetsBeyondIntegerRange() {
        TwoPointers.PairMatch match = TwoPointers.findPairWithSum(
            SortedIntArray.copyOf(new int[] {Integer.MAX_VALUE, Integer.MAX_VALUE}),
            4_294_967_294L
        ).orElseThrow(() -> new AssertionError("Expected match for large long target"));

        assertEquals(4_294_967_294L, match.sum(), "large target sum");
    }

    private static void shouldDefensivelyCopyValidatedInput() {
        int[] sourceValues = {1, 2, 3, 9};
        SortedIntArray sortedValues = SortedIntArray.copyOf(sourceValues);
        sourceValues[3] = -10;

        if (!TwoPointers.hasPairWithSum(sortedValues, 10)) {
            throw new AssertionError("Expected validated copy to be isolated from caller mutations");
        }
    }

    private static void assertEquals(long expected, long actual, String fieldName) {
        if (expected != actual) {
            throw new AssertionError(
                "Unexpected " + fieldName + ": expected " + expected + ", got " + actual);
        }
    }
}
