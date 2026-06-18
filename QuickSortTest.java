public final class QuickSortTest {
    private QuickSortTest() {
    }

    public static void main(String[] args) {
        for (SortScenario scenario : sortScenarios()) {
            assertSorted(scenario);
        }

        assertSortedCopyDoesNotMutateInput();
        assertNullRejected();
    }

    private static SortScenario[] sortScenarios() {
        return new SortScenario[] {
            new SortScenario(
                    "sample values",
                    new int[] {10, 7, 8, 9, 1, 5},
                    new int[] {1, 5, 7, 8, 9, 10}),
            new SortScenario("empty array", new int[] {}, new int[] {}),
            new SortScenario("single value", new int[] {4}, new int[] {4}),
            new SortScenario(
                    "duplicate values",
                    new int[] {3, 1, 3, 2, 1},
                    new int[] {1, 1, 2, 3, 3}),
            new SortScenario(
                    "all equal values",
                    new int[] {7, 7, 7, 7},
                    new int[] {7, 7, 7, 7}),
            new SortScenario(
                    "many duplicate values",
                    new int[] {4, 2, 4, 4, 1, 2, 4},
                    new int[] {1, 2, 2, 4, 4, 4, 4}),
            new SortScenario(
                    "negative values",
                    new int[] {0, -4, 8, -1},
                    new int[] {-4, -1, 0, 8}),
            new SortScenario(
                    "already sorted values",
                    new int[] {1, 2, 3, 4, 5},
                    new int[] {1, 2, 3, 4, 5}),
            new SortScenario(
                    "reverse sorted values",
                    new int[] {5, 4, 3, 2, 1},
                    new int[] {1, 2, 3, 4, 5})
        };
    }

    private static void assertSorted(SortScenario scenario) {
        int[] values = scenario.inputValues.clone();
        QuickSort.sort(values);

        TestAssertions.assertIntArrayEquals(scenario.name, values, scenario.expectedValues);
    }

    private static void assertNullRejected() {
        TestAssertions.assertThrowsNullPointer("sort null values", () -> QuickSort.sort(null));
        TestAssertions.assertThrowsNullPointer("copy null values", () -> QuickSort.sortedCopy(null));
    }

    private static void assertSortedCopyDoesNotMutateInput() {
        int[] inputValues = {3, 1, 2};
        int[] sortedValues = QuickSort.sortedCopy(inputValues);

        TestAssertions.assertIntArrayEquals(
                "sorted copy should return sorted values", sortedValues, new int[] {1, 2, 3});
        TestAssertions.assertIntArrayEquals(
                "sorted copy should not mutate input values", inputValues, new int[] {3, 1, 2});
    }

    private static final class SortScenario {
        private final String name;
        private final int[] inputValues;
        private final int[] expectedValues;

        private SortScenario(String name, int[] inputValues, int[] expectedValues) {
            this.name = name;
            this.inputValues = inputValues;
            this.expectedValues = expectedValues;
        }
    }
}
