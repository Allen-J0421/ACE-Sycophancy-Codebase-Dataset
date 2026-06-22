package sorting;

import java.util.Arrays;

public final class InsertionSortSelfTest {

    private InsertionSortSelfTest() {
        // No instances.
    }

    public static void main(String[] args) {
        verifiesFullArraySorting();
        verifiesSortedCopyDoesNotMutateInput();
        verifiesPartialRangeSorting();
        verifiesSortedDetection();
        verifiesPartialRangeSortedDetection();
        verifiesInputValidation();

        System.out.println("All insertion sort checks passed.");
    }

    private static void verifiesFullArraySorting() {
        int[] values = {5, 2, 4, 6, 1, 3};

        InsertionSort.sort(values);

        assertArrayEquals(new int[] {1, 2, 3, 4, 5, 6}, values, "sort should order the full array");
    }

    private static void verifiesSortedCopyDoesNotMutateInput() {
        int[] original = {3, 1, 2};
        int[] sorted = InsertionSort.sortedCopy(original);

        assertArrayEquals(new int[] {3, 1, 2}, original, "sortedCopy should preserve the input");
        assertArrayEquals(new int[] {1, 2, 3}, sorted, "sortedCopy should return sorted values");
    }

    private static void verifiesPartialRangeSorting() {
        int[] values = {9, 4, 3, 2, 8};

        InsertionSort.sortRange(values, 1, 4);

        assertArrayEquals(new int[] {9, 2, 3, 4, 8}, values, "sortRange should only affect the requested slice");
    }

    private static void verifiesSortedDetection() {
        assertTrue(InsertionSort.isSorted(new int[] {1, 2, 2, 3}), "isSorted should accept ordered arrays");
        assertTrue(!InsertionSort.isSorted(new int[] {2, 1}), "isSorted should reject unordered arrays");
    }

    private static void verifiesPartialRangeSortedDetection() {
        assertTrue(
                InsertionSort.isSortedRange(new int[] {9, 1, 2, 3, 0}, 1, 4),
                "isSortedRange should inspect only the requested slice");
        assertTrue(
                !InsertionSort.isSortedRange(new int[] {9, 3, 2, 1, 0}, 1, 4),
                "isSortedRange should reject unordered slices");
    }

    private static void verifiesInputValidation() {
        assertThrows(() -> InsertionSort.sort(null), "sort should reject null input");
        assertThrows(() -> InsertionSort.sortRange(new int[] {1, 2, 3}, -1, 2), "sortRange should reject bad start");
        assertThrows(() -> InsertionSort.sortRange(new int[] {1, 2, 3}, 1, 4), "sortRange should reject bad end");
        assertThrows(
                () -> InsertionSort.isSortedRange(new int[] {1, 2, 3}, 2, 1),
                "isSortedRange should reject an inverted range");
    }

    private static void assertArrayEquals(int[] expected, int[] actual, String message) {
        if (!Arrays.equals(expected, actual)) {
            throw new AssertionError(
                    message + ": expected " + Arrays.toString(expected) + " but was " + Arrays.toString(actual));
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertThrows(ThrowingRunnable action, String message) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            return;
        } catch (Exception other) {
            throw new AssertionError(message + ": threw unexpected exception " + other, other);
        }

        throw new AssertionError(message + ": expected IllegalArgumentException");
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }
}
