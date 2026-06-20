import java.util.Arrays;

public final class RadixSortSelfTest {
    private RadixSortSelfTest() {
    }

    public static void main(String[] args) {
        verifiesInPlaceSort();
        verifiesSortedCopyLeavesSourceUntouched();
        verifiesEdgeCases();
        verifiesUnsupportedInputFails();

        System.out.println("RadixSort self-test passed.");
    }

    private static void verifiesInPlaceSort() {
        int[] values = {170, 45, 75, 90, 802, 24, 2, 66};

        RadixSort.sort(values);

        assertArrayEquals(
            new int[] {2, 24, 45, 66, 75, 90, 170, 802},
            values,
            "sort should order the input array in place"
        );
    }

    private static void verifiesSortedCopyLeavesSourceUntouched() {
        int[] original = {9, 1, 9, 3, 0};
        int[] sorted = RadixSort.sortedCopy(original);

        assertArrayEquals(
            new int[] {9, 1, 9, 3, 0},
            original,
            "sortedCopy should not mutate the source array"
        );
        assertArrayEquals(
            new int[] {0, 1, 3, 9, 9},
            sorted,
            "sortedCopy should return sorted values"
        );
    }

    private static void verifiesEdgeCases() {
        assertSortedCopyEquals(new int[0], new int[0], "empty arrays should stay empty");
        assertSortedCopyEquals(new int[] {7}, new int[] {7}, "single items should stay unchanged");
        assertSortedCopyEquals(
            new int[] {10, 10, 10, 10},
            new int[] {10, 10, 10, 10},
            "duplicate values should preserve multiplicity"
        );
        assertSortedCopyEquals(
            new int[] {1000, 1, 100, 10, 0},
            new int[] {0, 1, 10, 100, 1000},
            "mixed digit widths should sort correctly"
        );
    }

    private static void verifiesUnsupportedInputFails() {
        assertThrows(
            NullPointerException.class,
            () -> RadixSort.sort(null),
            "sort should reject null input"
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> RadixSort.sortedCopy(new int[] {1, -1, 2}),
            "sortedCopy should reject negative values"
        );
    }

    private static void assertSortedCopyEquals(
        int[] input,
        int[] expected,
        String message
    ) {
        assertArrayEquals(expected, RadixSort.sortedCopy(input), message);
    }

    private static void assertArrayEquals(int[] expected, int[] actual, String message) {
        if (!Arrays.equals(expected, actual)) {
            throw new AssertionError(
                message
                    + "\nExpected: "
                    + Arrays.toString(expected)
                    + "\nActual:   "
                    + Arrays.toString(actual)
            );
        }
    }

    private static void assertThrows(
        Class<? extends Throwable> expectedType,
        Runnable action,
        String message
    ) {
        try {
            action.run();
        } catch (Throwable error) {
            if (expectedType.isInstance(error)) {
                return;
            }

            throw new AssertionError(
                message
                    + "\nExpected exception: "
                    + expectedType.getSimpleName()
                    + "\nActual exception: "
                    + error.getClass().getSimpleName(),
                error
            );
        }

        throw new AssertionError(
            message + "\nExpected exception: " + expectedType.getSimpleName()
        );
    }
}
