import java.util.Arrays;

public final class BubbleSortTest {
    private BubbleSortTest() {
    }

    public static void main(String[] args) {
        verifiesSortMutatesUnorderedValues();
        verifiesSortLeavesSortedValuesUntouched();
        verifiesSortHandlesDuplicates();
        verifiesSortHandlesNegativeValues();
        verifiesSortHandlesSingleElementValues();
        verifiesSortHandlesEmptyValues();
        verifiesSortedCopyReturnsSortedValues();
        verifiesSortedCopyDoesNotMutateInput();
        verifiesFormatterFormatsArrayForDisplay();
        verifiesFormatterFormatsEmptyArrayForDisplay();
        verifiesSortRejectsNullValues();
        verifiesSortedCopyRejectsNullValues();
        verifiesFormatterRejectsNullValues();
    }

    private static void verifiesSortMutatesUnorderedValues() {
        assertInPlaceSortResult(
            new int[] {64, 34, 25, 12, 22, 11, 90},
            new int[] {11, 12, 22, 25, 34, 64, 90}
        );
    }

    private static void verifiesSortLeavesSortedValuesUntouched() {
        assertInPlaceSortResult(new int[] {1, 2, 3, 4, 5}, new int[] {1, 2, 3, 4, 5});
    }

    private static void verifiesSortHandlesDuplicates() {
        assertInPlaceSortResult(new int[] {3, 1, 2, 3, 1}, new int[] {1, 1, 2, 3, 3});
    }

    private static void verifiesSortHandlesNegativeValues() {
        assertInPlaceSortResult(new int[] {4, -2, 0, -7, 3}, new int[] {-7, -2, 0, 3, 4});
    }

    private static void verifiesSortHandlesSingleElementValues() {
        assertInPlaceSortResult(new int[] {42}, new int[] {42});
    }

    private static void verifiesSortHandlesEmptyValues() {
        assertInPlaceSortResult(new int[] {}, new int[] {});
    }

    private static void verifiesSortedCopyReturnsSortedValues() {
        int[] sortedValues = BubbleSort.sortedCopy(new int[] {9, 4, 6, 1});

        assertArrayEquals(new int[] {1, 4, 6, 9}, sortedValues);
    }

    private static void verifiesSortedCopyDoesNotMutateInput() {
        int[] input = {9, 4, 6, 1};

        int[] sortedValues = BubbleSort.sortedCopy(input);

        assertArrayEquals(new int[] {9, 4, 6, 1}, input);
        assertArrayEquals(new int[] {1, 4, 6, 9}, sortedValues);
    }

    private static void verifiesFormatterFormatsArrayForDisplay() {
        String output = IntArrayFormatter.format(new int[] {7, 8, 9});

        assertEquals("7 8 9", output);
    }

    private static void verifiesFormatterFormatsEmptyArrayForDisplay() {
        String output = IntArrayFormatter.format(new int[] {});

        assertEquals("", output);
    }

    private static void verifiesSortRejectsNullValues() {
        assertThrowsNullPointer(() -> BubbleSort.sort(null));
    }

    private static void verifiesSortedCopyRejectsNullValues() {
        assertThrowsNullPointer(() -> BubbleSort.sortedCopy(null));
    }

    private static void verifiesFormatterRejectsNullValues() {
        assertThrowsNullPointer(() -> IntArrayFormatter.format(null));
    }

    private static void assertArrayEquals(int[] expected, int[] actual) {
        if (!Arrays.equals(expected, actual)) {
            throw new AssertionError(
                "Expected " + Arrays.toString(expected) + " but was " + Arrays.toString(actual)
            );
        }
    }

    private static void assertInPlaceSortResult(int[] input, int[] expected) {
        BubbleSort.sort(input);
        assertArrayEquals(expected, input);
    }

    private static void assertEquals(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected \"" + expected + "\" but was \"" + actual + "\"");
        }
    }

    private static void assertThrowsNullPointer(Runnable action) {
        try {
            action.run();
        } catch (NullPointerException expected) {
            return;
        }

        throw new AssertionError("Expected NullPointerException");
    }
}
