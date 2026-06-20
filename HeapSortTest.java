import java.util.Arrays;

public final class HeapSortTest {
    private static final int[][] SORT_CASES = {
            {},
            {1},
            {2, 1},
            {3, 3, 1, 2, 3},
            {-5, 0, -2, 7, 7},
            {1, 2, 3, 4, 5},
            {5, 4, 3, 2, 1}
    };

    private HeapSortTest() {
    }

    public static void main(String[] args) {
        sortsRepresentativeInputs();
        supportsOriginalMethodName();
        rejectsNullInput();
    }

    private static void sortsRepresentativeInputs() {
        for (int[] values : SORT_CASES) {
            assertSorted(Arrays.copyOf(values, values.length));
        }
    }

    private static void supportsOriginalMethodName() {
        int[] values = {4, 2, 1, 3};
        int[] expected = sortedCopy(values);

        HeapSort.heapSort(values);

        assertArrayEquals(expected, values);
    }

    private static void rejectsNullInput() {
        assertThrowsNullPointer(() -> HeapSort.sort(null));
        assertThrowsNullPointer(() -> HeapSort.heapSort(null));
    }

    private static void assertSorted(int[] values) {
        int[] expected = sortedCopy(values);

        HeapSort.sort(values);

        assertArrayEquals(expected, values);
    }

    private static int[] sortedCopy(int[] values) {
        int[] expected = Arrays.copyOf(values, values.length);
        Arrays.sort(expected);
        return expected;
    }

    private static void assertArrayEquals(int[] expected, int[] actual) {
        if (!Arrays.equals(expected, actual)) {
            throw new AssertionError(
                    "Expected " + Arrays.toString(expected) + " but got " + Arrays.toString(actual));
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
