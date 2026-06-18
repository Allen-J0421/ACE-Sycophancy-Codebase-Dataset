import java.util.Arrays;

public final class QuickSortTest {
    private QuickSortTest() {
    }

    public static void main(String[] args) {
        assertSorted(
                "sample values",
                new int[] {10, 7, 8, 9, 1, 5},
                new int[] {1, 5, 7, 8, 9, 10});
        assertSorted("empty array", new int[] {}, new int[] {});
        assertSorted("single value", new int[] {4}, new int[] {4});
        assertSorted(
                "duplicate values",
                new int[] {3, 1, 3, 2, 1},
                new int[] {1, 1, 2, 3, 3});
        assertSorted(
                "negative values",
                new int[] {0, -4, 8, -1},
                new int[] {-4, -1, 0, 8});
        assertNullRejected();
        assertFormattedValues();
    }

    private static void assertSorted(String scenario, int[] values, int[] expectedValues) {
        QuickSort.sort(values);

        if (!Arrays.equals(values, expectedValues)) {
            throw new AssertionError(
                    scenario
                            + ": expected "
                            + Arrays.toString(expectedValues)
                            + " but got "
                            + Arrays.toString(values));
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
}
