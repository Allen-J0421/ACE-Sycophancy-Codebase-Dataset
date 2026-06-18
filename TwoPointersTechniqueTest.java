import java.util.Arrays;

public final class TwoPointersTechniqueTest {

    private TwoPointersTechniqueTest() {
        // Test harness only.
    }

    public static void main(String[] args) {
        shouldFindPairInUnsortedInput();
        shouldRejectMissingPair();
        shouldHandleDuplicates();
        shouldNotMutateInput();
        shouldRejectInvalidInput();
        shouldAvoidOverflowFalsePositive();

        System.out.println("All tests passed.");
    }

    private static void shouldFindPairInUnsortedInput() {
        assertTrue(
            TwoPointersTechnique.hasPairWithSum(new int[] {2, -3, 1, 0, -1}, -2),
            "expected to find -3 + 1"
        );
    }

    private static void shouldRejectMissingPair() {
        assertFalse(
            TwoPointersTechnique.hasPairWithSum(new int[] {1, 4, 6, 9}, 20),
            "expected no pair to match"
        );
    }

    private static void shouldHandleDuplicates() {
        assertTrue(
            TwoPointersTechnique.hasPairWithSum(new int[] {3, 3, 7}, 6),
            "expected to use duplicate values"
        );
    }

    private static void shouldNotMutateInput() {
        int[] values = {5, 1, 4};
        int[] snapshot = values.clone();

        TwoPointersTechnique.hasPairWithSum(values, 9);

        assertTrue(Arrays.equals(values, snapshot), "expected input array to remain unchanged");
    }

    private static void shouldRejectInvalidInput() {
        assertFalse(TwoPointersTechnique.hasPairWithSum(null, 1), "expected null to return false");
        assertFalse(TwoPointersTechnique.hasPairWithSum(new int[] {42}, 42), "expected single item to return false");
    }

    private static void shouldAvoidOverflowFalsePositive() {
        assertFalse(
            TwoPointersTechnique.hasPairWithSum(
                new int[] {Integer.MAX_VALUE, Integer.MAX_VALUE},
                -2
            ),
            "expected long-based sum to avoid overflow"
        );
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError(message);
        }
    }
}
