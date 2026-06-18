import java.util.Arrays;

public final class QuickSortTest {
    private QuickSortTest() {
    }

    public static void main(String[] args) {
        for (SortScenario scenario : sortScenarios()) {
            assertSorted(scenario);
        }

        assertNullRejected();
        assertFormattedValues();
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
        QuickSort.sort(scenario.values);

        if (!Arrays.equals(scenario.values, scenario.expectedValues)) {
            throw new AssertionError(
                    scenario.name
                            + ": expected "
                            + Arrays.toString(scenario.expectedValues)
                            + " but got "
                            + Arrays.toString(scenario.values));
        }
    }

    private static void assertNullRejected() {
        try {
            QuickSort.sort(null);
            throw new AssertionError("null values should be rejected");
        } catch (NullPointerException expected) {
            // Expected by the public sorting contract.
        }
    }

    private static void assertFormattedValues() {
        String formattedValues = QuickSortDemo.formatValues(new int[] {1, 5, 7});

        if (!"1 5 7 ".equals(formattedValues)) {
            throw new AssertionError("formatted values should preserve the legacy console output");
        }
    }

    private static final class SortScenario {
        private final String name;
        private final int[] values;
        private final int[] expectedValues;

        private SortScenario(String name, int[] values, int[] expectedValues) {
            this.name = name;
            this.values = values;
            this.expectedValues = expectedValues;
        }
    }
}
