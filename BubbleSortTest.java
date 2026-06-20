import java.util.Arrays;

public final class BubbleSortTest {
    private BubbleSortTest() {
    }

    public static void main(String[] args) {
        verifiesSortsUnorderedValues();
        verifiesLeavesSortedValuesUntouched();
        verifiesHandlesDuplicates();
        verifiesHandlesNegativeValues();
        verifiesHandlesSingleElementValues();
        verifiesHandlesEmptyValues();
        verifiesFormatsArrayForDisplay();
        verifiesFormatsEmptyArrayForDisplay();
        verifiesSortRejectsNullValues();
        verifiesDisplayStringRejectsNullValues();
    }

    private static void verifiesSortsUnorderedValues() {
        assertSortedResult(
            new int[] {64, 34, 25, 12, 22, 11, 90},
            new int[] {11, 12, 22, 25, 34, 64, 90}
        );
    }

    private static void verifiesLeavesSortedValuesUntouched() {
        assertSortedResult(new int[] {1, 2, 3, 4, 5}, new int[] {1, 2, 3, 4, 5});
    }

    private static void verifiesHandlesDuplicates() {
        assertSortedResult(new int[] {3, 1, 2, 3, 1}, new int[] {1, 1, 2, 3, 3});
    }

    private static void verifiesHandlesNegativeValues() {
        assertSortedResult(new int[] {4, -2, 0, -7, 3}, new int[] {-7, -2, 0, 3, 4});
    }

    private static void verifiesHandlesSingleElementValues() {
        assertSortedResult(new int[] {42}, new int[] {42});
    }

    private static void verifiesHandlesEmptyValues() {
        assertSortedResult(new int[] {}, new int[] {});
    }

    private static void verifiesFormatsArrayForDisplay() {
        String output = BubbleSort.toDisplayString(new int[] {7, 8, 9});

        assertEquals("7 8 9", output);
    }

    private static void verifiesFormatsEmptyArrayForDisplay() {
        String output = BubbleSort.toDisplayString(new int[] {});

        assertEquals("", output);
    }

    private static void verifiesSortRejectsNullValues() {
        assertThrowsNullPointer(() -> BubbleSort.sort(null));
    }

    private static void verifiesDisplayStringRejectsNullValues() {
        assertThrowsNullPointer(() -> BubbleSort.toDisplayString(null));
    }

    private static void assertArrayEquals(int[] expected, int[] actual) {
        if (!Arrays.equals(expected, actual)) {
            throw new AssertionError(
                "Expected " + Arrays.toString(expected) + " but was " + Arrays.toString(actual)
            );
        }
    }

    private static void assertSortedResult(int[] input, int[] expected) {
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
