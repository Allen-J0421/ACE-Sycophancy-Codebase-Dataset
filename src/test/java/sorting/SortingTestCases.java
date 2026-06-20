package sorting;

final class SortingTestCases {
    private SortingTestCases() {
    }

    static SortCase[] inPlaceSortCases() {
        return new SortCase[] {
            new SortCase(SortingExamples.demoValues(), SortingExamples.sortedDemoValues()),
            new SortCase(SortingExamples.sortedValues(), SortingExamples.sortedValues()),
            new SortCase(SortingExamples.duplicateValues(), SortingExamples.sortedDuplicateValues()),
            new SortCase(SortingExamples.negativeValues(), SortingExamples.sortedNegativeValues()),
            new SortCase(SortingExamples.singleValue(), SortingExamples.singleValue()),
            new SortCase(SortingExamples.emptyValues(), SortingExamples.emptyValues())
        };
    }

    record SortCase(int[] input, int[] expected) {
    }
}
