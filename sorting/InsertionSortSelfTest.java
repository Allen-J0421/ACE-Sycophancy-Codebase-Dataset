package sorting;

import java.util.Arrays;
import java.util.Comparator;

public final class InsertionSortSelfTest {

    private InsertionSortSelfTest() {
        // Utility class.
    }

    public static void main(String[] args) {
        testIntArraySort();
        testIntArrayRangeSort();
        testObjectArraySort();
        testCustomComparatorSort();
        testSortedCopyDoesNotMutateInput();
        testNullHandling();
        testRangeValidation();
    }

    private static void testIntArraySort() {
        int[] values = {12, 11, 13, 5, 6};

        InsertionSort.sort(values);

        assertArrayEquals(new int[] {5, 6, 11, 12, 13}, values, "int array sort");
    }

    private static void testIntArrayRangeSort() {
        int[] values = {9, 4, 3, 8, 7};

        InsertionSort.sort(values, 1, 4);

        assertArrayEquals(new int[] {9, 3, 4, 8, 7}, values, "int range sort");
    }

    private static void testObjectArraySort() {
        Integer[] values = {12, 11, 13, 5, 6};

        InsertionSort.sort(values);

        assertArrayEquals(new Integer[] {5, 6, 11, 12, 13}, values, "object sort");
    }

    private static void testCustomComparatorSort() {
        Integer[] values = {12, 11, 13, 5, 6};

        InsertionSort.sort(values, Comparator.reverseOrder());

        assertArrayEquals(new Integer[] {13, 12, 11, 6, 5}, values, "custom comparator sort");
    }

    private static void testSortedCopyDoesNotMutateInput() {
        Integer[] values = {12, 11, 13, 5, 6};

        Integer[] sorted = InsertionSort.sortedCopy(values);

        assertArrayEquals(new Integer[] {12, 11, 13, 5, 6}, values, "sortedCopy input preservation");
        assertArrayEquals(new Integer[] {5, 6, 11, 12, 13}, sorted, "sortedCopy output");
    }

    private static void testNullHandling() {
        assertThrows(NullPointerException.class, () -> InsertionSort.sort((int[]) null), "null int sort");
        assertThrows(NullPointerException.class, () -> InsertionSort.sortedCopy((int[]) null), "null int sortedCopy");
        assertThrows(NullPointerException.class, () -> InsertionSort.sort((Integer[]) null), "null object sort");
        assertThrows(NullPointerException.class, () -> InsertionSort.sortedCopy((Integer[]) null), "null object sortedCopy");
        assertThrows(NullPointerException.class, () -> InsertionSort.sort(new Integer[] {1}, null), "null comparator sort");
        assertThrows(NullPointerException.class, () -> InsertionSort.sortedCopy(new Integer[] {1}, null), "null comparator sortedCopy");
    }

    private static void testRangeValidation() {
        int[] values = {9, 4, 3, 8, 7};

        assertThrows(IndexOutOfBoundsException.class, () -> InsertionSort.sort(values, -1, 3), "negative fromIndex");
        assertThrows(IndexOutOfBoundsException.class, () -> InsertionSort.sort(values, 0, 6), "toIndex past end");
        assertThrows(IndexOutOfBoundsException.class, () -> InsertionSort.sort(values, 4, 2), "fromIndex greater than toIndex");
    }

    private static void assertArrayEquals(int[] expected, int[] actual, String label) {
        if (!Arrays.equals(expected, actual)) {
            throw new AssertionError(label + " expected " + Arrays.toString(expected)
                    + " but was " + Arrays.toString(actual));
        }
    }

    private static <T> void assertArrayEquals(T[] expected, T[] actual, String label) {
        if (!Arrays.equals(expected, actual)) {
            throw new AssertionError(label + " expected " + Arrays.toString(expected)
                    + " but was " + Arrays.toString(actual));
        }
    }

    private static void assertThrows(Class<? extends Throwable> expectedType, ThrowingRunnable action, String label) {
        try {
            action.run();
        } catch (Throwable actual) {
            if (expectedType.isInstance(actual)) {
                return;
            }

            throw new AssertionError(label + " expected " + expectedType.getSimpleName()
                    + " but threw " + actual.getClass().getSimpleName(), actual);
        }

        throw new AssertionError(label + " expected " + expectedType.getSimpleName() + " but no exception was thrown");
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run();
    }
}
