package sorting;

import java.util.Arrays;

final class IntArraySlice {

    private final int[] values;
    private final IndexRange range;

    private IntArraySlice(int[] values, IndexRange range) {
        this.values = values;
        this.range = range;
    }

    static IntArraySlice entireArray(int[] values) {
        requireArray(values);
        return new IntArraySlice(values, IndexRange.forArray(values.length));
    }

    static IntArraySlice of(int[] values, int startInclusive, int endExclusive) {
        requireArray(values);
        return new IntArraySlice(values, IndexRange.of(values.length, startInclusive, endExclusive));
    }

    void sortWithInsertion() {
        if (range.hasFewerThanTwoElements()) {
            return;
        }

        for (int currentIndex = range.startInclusive() + 1;
                currentIndex < range.endExclusive();
                currentIndex++) {
            insertValue(currentIndex);
        }
    }

    boolean isSorted() {
        for (int index = range.startInclusive() + 1; index < range.endExclusive(); index++) {
            if (values[index - 1] > values[index]) {
                return false;
            }
        }

        return true;
    }

    int[] sortedCopy() {
        int[] copy = Arrays.copyOf(values, values.length);
        new IntArraySlice(copy, range).sortWithInsertion();
        return copy;
    }

    private void insertValue(int currentIndex) {
        int valueToInsert = values[currentIndex];
        int insertionIndex = findInsertionIndex(currentIndex, valueToInsert);

        shiftRight(insertionIndex, currentIndex);
        values[insertionIndex] = valueToInsert;
    }

    private int findInsertionIndex(int currentIndex, int valueToInsert) {
        int scanIndex = currentIndex - 1;

        while (scanIndex >= range.startInclusive() && values[scanIndex] > valueToInsert) {
            scanIndex--;
        }

        return scanIndex + 1;
    }

    private void shiftRight(int startIndex, int endIndex) {
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
