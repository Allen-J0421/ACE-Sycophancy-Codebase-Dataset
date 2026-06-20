package sorting;

final class SortingTestCases {
    private SortingTestCases() {
    }

    static SortCase[] inPlaceSortCases() {
        SortScenario[] scenarios = SortingExamples.inPlaceSortScenarios();
        SortCase[] sortCases = new SortCase[scenarios.length];

        for (int index = 0; index < scenarios.length; index++) {
            sortCases[index] = new SortCase(
                scenarios[index].name(),
                scenarios[index].input(),
                scenarios[index].expected()
            );
        }

        return sortCases;
    }

    record SortCase(String name, int[] input, int[] expected) {
    }
}
