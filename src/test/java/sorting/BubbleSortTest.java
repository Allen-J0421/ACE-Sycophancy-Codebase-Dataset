package sorting;

import java.util.Arrays;

public final class BubbleSortTest {
    private BubbleSortTest() {
    }

    public static void main(String[] args) {
        verifiesInPlaceSortCases();
        verifiesSortedCopyReturnsSortedValues();
        verifiesSortedCopyDoesNotMutateInput();
        verifiesFormatterFormatsArrayForDisplay();
        verifiesFormatterFormatsEmptyArrayForDisplay();
        verifiesSortRejectsNullValues();
        verifiesSortedCopyRejectsNullValues();
        verifiesFormatterRejectsNullValues();
    }

    private static void verifiesInPlaceSortCases() {
        for (SortCase sortCase : inPlaceSortCases()) {
            assertInPlaceSortResult(sortCase.input(), sortCase.expected());
        }
    }

    private static void verifiesSortedCopyReturnsSortedValues() {
        int[] sortedValues = BubbleSort.sortedCopy(SortingExamples.copySortValues());

        assertArrayEquals(SortingExamples.sortedCopySortValues(), sortedValues);
    }

    private static void verifiesSortedCopyDoesNotMutateInput() {
        int[] input = SortingExamples.copySortValues();

        int[] sortedValues = BubbleSort.sortedCopy(input);

        assertArrayEquals(SortingExamples.copySortValues(), input);
        assertArrayEquals(SortingExamples.sortedCopySortValues(), sortedValues);
    }

    private static void verifiesFormatterFormatsArrayForDisplay() {
        String output = IntArrayFormatter.format(SortingExamples.formatValues());

        assertEquals("7 8 9", output);
    }

    private static void verifiesFormatterFormatsEmptyArrayForDisplay() {
        String output = IntArrayFormatter.format(SortingExamples.emptyValues());

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

    private static SortCase[] inPlaceSortCases() {
        return new SortCase[] {
            new SortCase(SortingExamples.demoValues(), SortingExamples.sortedDemoValues()),
            new SortCase(SortingExamples.sortedValues(), SortingExamples.sortedValues()),
            new SortCase(SortingExamples.duplicateValues(), SortingExamples.sortedDuplicateValues()),
            new SortCase(SortingExamples.negativeValues(), SortingExamples.sortedNegativeValues()),
            new SortCase(SortingExamples.singleValue(), SortingExamples.singleValue()),
            new SortCase(SortingExamples.emptyValues(), SortingExamples.emptyValues())
        };
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

    private record SortCase(int[] input, int[] expected) {
    }
}
