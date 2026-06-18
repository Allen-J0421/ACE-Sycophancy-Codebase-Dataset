public final class TwoPointersTest {

    private TwoPointersTest() {
    }

    public static void main(String[] args) {
        shouldFindMatchingPair();
        shouldReturnEmptyWhenNoPairExists();
        shouldRejectUnsortedInput();
        shouldHandleIntegerOverflowSafely();
    }

    private static void shouldFindMatchingPair() {
        TwoPointers.PairMatch match = TwoPointers.findPairWithSum(
            new int[] {-3, -1, 0, 1, 2},
            -2
        ).orElseThrow(() -> new AssertionError("Expected a matching pair"));

        assertEquals(0, match.leftIndex(), "left index");
        assertEquals(3, match.rightIndex(), "right index");
        assertEquals(-3, match.leftValue(), "left value");
        assertEquals(1, match.rightValue(), "right value");
        assertEquals(-2, match.sum(), "pair sum");
    }

    private static void shouldReturnEmptyWhenNoPairExists() {
        if (TwoPointers.findPairWithSum(new int[] {1, 4, 8}, 6).isPresent()) {
            throw new AssertionError("Expected no matching pair");
        }
    }

    private static void shouldRejectUnsortedInput() {
        try {
            TwoPointers.findPairWithSum(new int[] {3, 1, 2}, 4);
            throw new AssertionError("Expected unsorted input to be rejected");
        } catch (IllegalArgumentException expected) {
            if (!expected.getMessage().contains("sorted")) {
                throw new AssertionError("Unexpected validation message: " + expected.getMessage());
            }
        }
    }

    private static void shouldHandleIntegerOverflowSafely() {
        TwoPointers.PairMatch match = TwoPointers.findPairWithSum(
            new int[] {-1, Integer.MAX_VALUE},
            Integer.MAX_VALUE - 1
        ).orElseThrow(() -> new AssertionError("Expected overflow-safe matching pair"));

        assertEquals(Integer.MAX_VALUE - 1L, match.sum(), "overflow-safe sum");
    }

    private static void assertEquals(long expected, long actual, String fieldName) {
        if (expected != actual) {
            throw new AssertionError(
                "Unexpected " + fieldName + ": expected " + expected + ", got " + actual);
        }
    }
}
