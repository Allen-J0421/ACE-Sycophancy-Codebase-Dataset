import java.util.Objects;

public final class BinarySearchTest {
    private static final int FIRST_INDEX = 0;
    private static final int MIDDLE_INDEX = 3;
    private static final int LAST_INDEX = 4;
    private static final int FIRST_TARGET = 2;
    private static final int MIDDLE_TARGET = 10;
    private static final int LAST_TARGET = 40;
    private static final int MISSING_TARGET = 5;

    private BinarySearchTest() {
    }

    public static void main(String[] args) {
        findsTargetInMiddle();
        findsTargetAtStart();
        findsTargetAtEnd();
        returnsNotFoundForMissingTarget();
        returnsNotFoundForEmptyArray();
        rejectsNullArray();
    }

    private static void findsTargetInMiddle() {
        assertSearchResult(MIDDLE_INDEX, sampleNumbers(), MIDDLE_TARGET);
    }

    private static void findsTargetAtStart() {
        assertSearchResult(FIRST_INDEX, sampleNumbers(), FIRST_TARGET);
    }

    private static void findsTargetAtEnd() {
        assertSearchResult(LAST_INDEX, sampleNumbers(), LAST_TARGET);
    }

    private static void returnsNotFoundForMissingTarget() {
        assertSearchResult(BinarySearch.NOT_FOUND, sampleNumbers(), MISSING_TARGET);
    }

    private static void returnsNotFoundForEmptyArray() {
        assertSearchResult(BinarySearch.NOT_FOUND, new int[] {}, MIDDLE_TARGET);
    }

    private static void rejectsNullArray() {
        try {
            BinarySearch.binarySearch(null, MIDDLE_TARGET);
            throw new AssertionError("Expected NullPointerException for null input");
        } catch (NullPointerException exception) {
            assertEquals("sortedNumbers must not be null", exception.getMessage(), "null input message");
        }
    }

    private static void assertSearchResult(int expected, int[] sortedNumbers, int target) {
        int actual = BinarySearch.binarySearch(sortedNumbers, target);
        assertEquals(expected, actual, "search result");
    }

    private static void assertEquals(Object expected, Object actual, String context) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(
                    context + ": expected <" + expected + "> but was <" + actual + ">");
        }
    }

    private static int[] sampleNumbers() {
        return new int[] {2, 3, 4, 10, 40};
    }
}
