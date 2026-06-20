package radixsort;

import java.util.Arrays;

public final class RadixSortSelfTest {
    private RadixSortSelfTest() {
        // Utility class.
    }

    public static void main(String[] args) {
        assertSort(new int[] { 170, 45, 75, 90, 802, 24, 2, 66 });
        assertSort(new int[] { -3, 0, -1, 8, -10, 5 });
        assertSort(new int[] { Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE });
        assertSortedCopyLeavesSourceUntouched(new int[] { 9, 1, 5, 3 });
        System.out.println("RadixSortSelfTest passed");
    }

    private static void assertSort(int[] input) {
        int[] actual = input.clone();
        int[] expected = input.clone();

        RadixSort.sort(actual);
        Arrays.sort(expected);

        if (!Arrays.equals(actual, expected)) {
            throw new AssertionError(
                    "Expected " + Arrays.toString(expected) + " but got " + Arrays.toString(actual));
        }
    }

    private static void assertSortedCopyLeavesSourceUntouched(int[] input) {
        int[] actualSource = input.clone();
        int[] expectedSource = input.clone();
        int[] expectedSorted = input.clone();

        int[] sorted = RadixSort.sortedCopy(actualSource);
        Arrays.sort(expectedSorted);

        if (!Arrays.equals(actualSource, expectedSource)) {
            throw new AssertionError("sortedCopy mutated its input");
        }
        if (!Arrays.equals(sorted, expectedSorted)) {
            throw new AssertionError(
                    "Expected " + Arrays.toString(expectedSorted) + " but got " + Arrays.toString(sorted));
        }
    }
}
