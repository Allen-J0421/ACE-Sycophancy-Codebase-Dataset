import java.util.Arrays;

public final class BubbleSortTest {
    private BubbleSortTest() {
    }

    public static void main(String[] args) {
        verifiesSortsUnorderedValues();
        verifiesLeavesSortedValuesUntouched();
        verifiesHandlesDuplicates();
        verifiesHandlesSingleElementValues();
        verifiesHandlesEmptyValues();
        verifiesFormatsArrayForDisplay();
        verifiesSortRejectsNullValues();
        verifiesDisplayStringRejectsNullValues();
    }

    private static void verifiesSortsUnorderedValues() {
        int[] values = {64, 34, 25, 12, 22, 11, 90};

        BubbleSort.sort(values);

        assertArrayEquals(new int[] {11, 12, 22, 25, 34, 64, 90}, values);
    }

    private static void verifiesLeavesSortedValuesUntouched() {
        int[] values = {1, 2, 3, 4, 5};

        BubbleSort.sort(values);

        assertArrayEquals(new int[] {1, 2, 3, 4, 5}, values);
    }

    private static void verifiesHandlesDuplicates() {
        int[] values = {3, 1, 2, 3, 1};

        BubbleSort.sort(values);

        assertArrayEquals(new int[] {1, 1, 2, 3, 3}, values);
    }

    private static void verifiesHandlesSingleElementValues() {
        int[] values = {42};

        BubbleSort.sort(values);

        assertArrayEquals(new int[] {42}, values);
    }

    private static void verifiesHandlesEmptyValues() {
        int[] values = {};

        BubbleSort.sort(values);

        assertArrayEquals(new int[] {}, values);
    }

    private static void verifiesFormatsArrayForDisplay() {
        String output = BubbleSort.toDisplayString(new int[] {7, 8, 9});

        assertEquals("7 8 9", output);
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
