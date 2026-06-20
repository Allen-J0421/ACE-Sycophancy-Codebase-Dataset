package sorting;

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
        for (SortingTestCases.SortCase sortCase : SortingTestCases.inPlaceSortCases()) {
            assertInPlaceSortResult(sortCase.input(), sortCase.expected());
        }
    }

    private static void verifiesSortedCopyReturnsSortedValues() {
        SortScenario scenario = SortingExamples.copySortScenario();
        int[] sortedValues = BubbleSort.sortedCopy(scenario.input());

        IntArrayAssertions.assertArrayEquals(scenario.expected(), sortedValues);
    }

    private static void verifiesSortedCopyDoesNotMutateInput() {
        SortScenario scenario = SortingExamples.copySortScenario();
        int[] input = scenario.input();

        int[] sortedValues = BubbleSort.sortedCopy(input);

        IntArrayAssertions.assertArrayEquals(scenario.input(), input);
        IntArrayAssertions.assertArrayEquals(scenario.expected(), sortedValues);
    }

    private static void verifiesFormatterFormatsArrayForDisplay() {
        String output = IntArrayFormatter.format(SortingExamples.formatValues());

        IntArrayAssertions.assertEquals("7 8 9", output);
    }

    private static void verifiesFormatterFormatsEmptyArrayForDisplay() {
        String output = IntArrayFormatter.format(SortingExamples.emptyValues());

        IntArrayAssertions.assertEquals("", output);
    }

    private static void verifiesSortRejectsNullValues() {
        IntArrayAssertions.assertThrowsNullPointer(() -> BubbleSort.sort(null));
    }

    private static void verifiesSortedCopyRejectsNullValues() {
        IntArrayAssertions.assertThrowsNullPointer(() -> BubbleSort.sortedCopy(null));
    }

    private static void verifiesFormatterRejectsNullValues() {
        IntArrayAssertions.assertThrowsNullPointer(() -> IntArrayFormatter.format(null));
    }

    private static void assertInPlaceSortResult(int[] input, int[] expected) {
        BubbleSort.sort(input);
        IntArrayAssertions.assertArrayEquals(expected, input);
    }
}
