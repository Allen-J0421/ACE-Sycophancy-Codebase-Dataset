package sorting;

import java.util.Arrays;

public final class InsertionSort {

    private InsertionSort() {
        // Utility class.
    }

    public static void sort(int[] values) {
        requireArray(values);
        sort(values, IndexRange.forArray(values.length));
    }

    public static void sortRange(int[] values, int startInclusive, int endExclusive) {
        requireArray(values);
        sort(values, IndexRange.of(values.length, startInclusive, endExclusive));
    }

    public static boolean isSorted(int[] values) {
        requireArray(values);
        return isSorted(values, IndexRange.forArray(values.length));
    }

    public static boolean isSortedRange(int[] values, int startInclusive, int endExclusive) {
        requireArray(values);
        return isSorted(values, IndexRange.of(values.length, startInclusive, endExclusive));
    }

    public static int[] sortedCopy(int[] values) {
        requireArray(values);

        int[] copy = Arrays.copyOf(values, values.length);
        sort(copy);
        return copy;
    }

    private static void sort(int[] values, IndexRange range) {
        if (range.hasFewerThanTwoElements()) {
            return;
        }

        for (int currentIndex = range.startInclusive() + 1;
                currentIndex < range.endExclusive();
                currentIndex++) {
            insertValue(values, range.startInclusive(), currentIndex);
        }
    }

    private static boolean isSorted(int[] values, IndexRange range) {
        for (int index = range.startInclusive() + 1; index < range.endExclusive(); index++) {
            if (values[index - 1] > values[index]) {
                return false;
            }
        }

        return true;
    }

    private static void insertValue(int[] values, int startInclusive, int currentIndex) {
        int valueToInsert = values[currentIndex];
        int insertionIndex = findInsertionIndex(values, startInclusive, currentIndex, valueToInsert);

        shiftRight(values, insertionIndex, currentIndex);
        values[insertionIndex] = valueToInsert;
    }

    private static int findInsertionIndex(
            int[] values,
            int startInclusive,
            int currentIndex,
            int valueToInsert) {
        int scanIndex = currentIndex - 1;

        while (scanIndex >= startInclusive && values[scanIndex] > valueToInsert) {
            scanIndex--;
        }

        return scanIndex + 1;
    }

    private static void shiftRight(int[] values, int startIndex, int endIndex) {
        int length = endIndex - startIndex;
        if (length > 0) {
            System.arraycopy(values, startIndex, values, startIndex + 1, length);
        }
    }

    private static void requireArray(int[] values) {
        if (values == null) {
            throw new IllegalArgumentException("values must not be null");
        }
    }
}
