public final class TwoPointersTest {

    private TwoPointersTest() {
    }

    public static void main(String[] args) {
        shouldFindMatchingPair();
        shouldReturnEmptyWhenNoPairExists();
        shouldReturnEmptyForArraysWithFewerThanTwoValues();
        shouldReturnEmptyWhenTargetIsOutsidePossibleRange();
        shouldRejectUnsortedInput();
        shouldHandleIntegerOverflowSafely();
        shouldSupportTargetsBeyondIntegerRange();
        shouldDefensivelyCopyValidatedInput();
        shouldRejectNullSortedValueObjects();
        shouldBehaveLikeAValueObject();
    }

    private static void shouldFindMatchingPair() {
        SortedIntArray sortedValues = SortedIntArray.of(-3, -1, 0, 1, 2);
        PairMatch match = sortedValues.findPairWithSum(-2)
            .orElseThrow(() -> new AssertionError("Expected a matching pair"));

        assertMatch(match, 0, 3, -3, 1, -2);
    }

    private static void shouldReturnEmptyWhenNoPairExists() {
        assertNoMatch(SortedIntArray.of(1, 4, 8).findPairWithSum(6));
    }

    private static void shouldReturnEmptyForArraysWithFewerThanTwoValues() {
        assertNoMatch(SortedIntArray.of().findPairWithSum(0));
        assertNoMatch(SortedIntArray.of(5).findPairWithSum(10));
    }

    private static void shouldReturnEmptyWhenTargetIsOutsidePossibleRange() {
        SortedIntArray sortedValues = SortedIntArray.of(2, 4, 8, 16);

        assertNoMatch(sortedValues.findPairWithSum(5));
        assertNoMatch(sortedValues.findPairWithSum(30));
    }

    private static void shouldRejectUnsortedInput() {
        assertThrowsWithMessage(
            () -> SortedIntArray.of(3, 1, 2),
            "sorted",
            "Expected unsorted input to be rejected");
    }

    private static void shouldHandleIntegerOverflowSafely() {
        PairMatch match = SortedIntArray.of(-1, Integer.MAX_VALUE)
            .findPairWithSum(Integer.MAX_VALUE - 1)
            .orElseThrow(() -> new AssertionError("Expected overflow-safe matching pair"));

        assertEquals(Integer.MAX_VALUE - 1L, match.sum(), "overflow-safe sum");
    }

    private static void shouldSupportTargetsBeyondIntegerRange() {
        PairMatch match = SortedIntArray.of(Integer.MAX_VALUE, Integer.MAX_VALUE)
            .findPairWithSum(4_294_967_294L)
            .orElseThrow(() -> new AssertionError("Expected match for large long target"));

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

    private static void shouldRejectNullSortedValueObjects() {
        assertThrowsWithMessage(
            () -> TwoPointers.findPairWithSum((SortedIntArray) null, 0),
            "must not be null",
            "Expected null SortedIntArray to be rejected");
    }

    private static void shouldBehaveLikeAValueObject() {
        SortedIntArray first = SortedIntArray.of(1, 2, 3);
        SortedIntArray second = SortedIntArray.copyOf(new int[] {1, 2, 3});

        if (!first.equals(second)) {
            throw new AssertionError("Expected equal sorted arrays to compare by value");
        }

        assertEquals(first.hashCode(), second.hashCode(), "hash code");

        if (!"[1, 2, 3]".equals(first.toString())) {
            throw new AssertionError("Unexpected string representation: " + first);
        }
    }

    private static void assertMatch(
        PairMatch match,
        int expectedLeftIndex,
        int expectedRightIndex,
        int expectedLeftValue,
        int expectedRightValue,
        long expectedSum
    ) {
        assertEquals(expectedLeftIndex, match.leftIndex(), "left index");
        assertEquals(expectedRightIndex, match.rightIndex(), "right index");
        assertEquals(expectedLeftValue, match.leftValue(), "left value");
        assertEquals(expectedRightValue, match.rightValue(), "right value");
        assertEquals(expectedSum, match.sum(), "pair sum");
    }

    private static void assertNoMatch(java.util.Optional<PairMatch> match) {
        if (match.isPresent()) {
            throw new AssertionError("Expected no matching pair");
        }
    }

    private static void assertThrowsWithMessage(
        ThrowingRunnable action,
        String expectedMessageFragment,
        String failureMessage
    ) {
        try {
            action.run();
            throw new AssertionError(failureMessage);
        } catch (IllegalArgumentException expected) {
            if (!expected.getMessage().contains(expectedMessageFragment)) {
                throw new AssertionError("Unexpected validation message: " + expected.getMessage());
            }
        }
    }

    private static void assertEquals(long expected, long actual, String fieldName) {
        if (expected != actual) {
            throw new AssertionError(
                "Unexpected " + fieldName + ": expected " + expected + ", got " + actual);
        }
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run();
    }
}
