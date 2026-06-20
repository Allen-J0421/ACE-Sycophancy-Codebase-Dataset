package sorting;

final class SortingExamples {
    private static final SortScenario DEMO_SCENARIO = new SortScenario(
        "demo values",
        new int[] {64, 34, 25, 12, 22, 11, 90},
        new int[] {11, 12, 22, 25, 34, 64, 90}
    );
    private static final SortScenario[] IN_PLACE_SORT_SCENARIOS = {
        DEMO_SCENARIO,
        new SortScenario("sorted values", new int[] {1, 2, 3, 4, 5}, new int[] {1, 2, 3, 4, 5}),
        new SortScenario("duplicate values", new int[] {3, 1, 2, 3, 1}, new int[] {1, 1, 2, 3, 3}),
        new SortScenario("negative values", new int[] {4, -2, 0, -7, 3}, new int[] {-7, -2, 0, 3, 4}),
        new SortScenario("single value", new int[] {42}, new int[] {42}),
        new SortScenario("empty values", new int[] {}, new int[] {})
    };
    private static final SortScenario COPY_SORT_SCENARIO = new SortScenario(
        "copy sort values",
        new int[] {9, 4, 6, 1},
        new int[] {1, 4, 6, 9}
    );
    private static final int[] FORMAT_VALUES = {7, 8, 9};
    private static final int[] EMPTY_VALUES = {};

    private SortingExamples() {
    }

    static SortScenario demoScenario() {
        return DEMO_SCENARIO;
    }

    static SortScenario[] inPlaceSortScenarios() {
        return IN_PLACE_SORT_SCENARIOS.clone();
    }

    static SortScenario copySortScenario() {
        return COPY_SORT_SCENARIO;
    }

    static int[] formatValues() {
        return FORMAT_VALUES.clone();
    }

    static int[] emptyValues() {
        return EMPTY_VALUES.clone();
    }
}
