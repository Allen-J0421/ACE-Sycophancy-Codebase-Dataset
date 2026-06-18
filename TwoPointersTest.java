public final class TwoPointersTest {

    private TwoPointersTest() {
    }

    public static void main(String[] args) {
        shouldFindMatchingPair();
        shouldReturnEmptyWhenNoPairExists();
        shouldReturnEmptyForArraysWithFewerThanTwoValues();
        shouldReturnEmptyWhenTargetIsOutsidePossibleRange();
        shouldSupportRawArrayFacadeLookups();
        shouldSupportRawArrayFacadeBooleanChecks();
        shouldRejectNullRawArrayFacadeInput();
        shouldRejectUnsortedRawArrayFacadeInput();
        shouldRejectUnsortedInput();
        shouldHandleIntegerOverflowSafely();
        shouldSupportTargetsBeyondIntegerRange();
        shouldDefensivelyCopyValidatedInput();
        shouldReuseEmptyInstance();
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

    private static void shouldSupportRawArrayFacadeLookups() {
        PairMatch match = TwoPointers.findPairWithSum(new int[] {-3, -1, 0, 1, 2}, -2)
            .orElseThrow(() -> new AssertionError("Expected raw array facade to find a match"));

        assertMatch(match, 0, 3, -3, 1, -2);
    }

    private static void shouldSupportRawArrayFacadeBooleanChecks() {
        assertTrue(
            TwoPointers.hasPairWithSum(new int[] {1, 3, 7, 9}, 10),
            "Expected raw array facade to report a matching pair");
        assertFalse(
            TwoPointers.hasPairWithSum(new int[] {1, 3, 7, 9}, 13),
            "Expected raw array facade to report no matching pair");
    }

    private static void shouldRejectNullRawArrayFacadeInput() {
        assertThrowsWithMessage(
            () -> TwoPointers.findPairWithSum((int[]) null, 0),
            "must not be null",
            "Expected null raw arrays to be rejected");
    }

    private static void shouldRejectUnsortedRawArrayFacadeInput() {
        assertThrowsWithMessage(
            () -> TwoPointers.hasPairWithSum(new int[] {3, 1, 2}, 4),
            "sorted",
            "Expected unsorted raw arrays to be rejected");
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

        assertTrue(
            TwoPointers.hasPairWithSum(sortedValues, 10),
            "Expected validated copy to be isolated from caller mutations");
    }

    private static void shouldReuseEmptyInstance() {
        SortedIntArray first = SortedIntArray.of();
        SortedIntArray second = SortedIntArray.copyOf(new int[0]);

        assertSame(first, second, "Expected empty sorted arrays to reuse the shared instance");
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

        assertTrue(first.equals(second), "Expected equal sorted arrays to compare by value");
        assertEquals(first.hashCode(), second.hashCode(), "hash code");
        assertEquals("[1, 2, 3]", first.toString(), "string representation");
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
        assertFalse(match.isPresent(), "Expected no matching pair");
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

    private static void assertEquals(String expected, String actual, String fieldName) {
        if (!expected.equals(actual)) {
            throw new AssertionError(
                "Unexpected " + fieldName + ": expected " + expected + ", got " + actual);
        }
    }

    private static void assertTrue(boolean condition, String failureMessage) {
        if (!condition) {
            throw new AssertionError(failureMessage);
        }
    }

    private static void assertFalse(boolean condition, String failureMessage) {
        if (condition) {
            throw new AssertionError(failureMessage);
        }
    }

    private static void assertSame(Object expected, Object actual, String failureMessage) {
        if (expected != actual) {
            throw new AssertionError(failureMessage);
        }
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run();
    }
}
