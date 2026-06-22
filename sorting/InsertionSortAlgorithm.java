package sorting;

final class InsertionSortAlgorithm {

    private InsertionSortAlgorithm() {
        // Utility class.
    }

    static void sort(int[] values) {
        sort(IntArraySlice.entireArray(values));
    }

    static void sort(int[] values, int startInclusive, int endExclusive) {
        sort(IntArraySlice.of(values, startInclusive, endExclusive));
    }

    static boolean isSorted(int[] values) {
        return isSorted(IntArraySlice.entireArray(values));
    }

    static boolean isSorted(int[] values, int startInclusive, int endExclusive) {
        return isSorted(IntArraySlice.of(values, startInclusive, endExclusive));
    }

    static int[] sortedCopy(int[] values) {
        IntArraySlice copy = IntArraySlice.entireArray(values).copy();
        sort(copy);
        return copy.values();
    }

    private static void sort(IntArraySlice slice) {
        if (slice.hasFewerThanTwoElements()) {
            return;
        }

        for (int currentIndex = slice.startInclusive() + 1;
                currentIndex < slice.endExclusive();
                currentIndex++) {
            insertValue(slice, currentIndex);
        }
    }

    private static boolean isSorted(IntArraySlice slice) {
        for (int index = slice.startInclusive() + 1; index < slice.endExclusive(); index++) {
            if (slice.valueAt(index - 1) > slice.valueAt(index)) {
                return false;
            }
        }

        return true;
    }

    private static void insertValue(IntArraySlice slice, int currentIndex) {
        int valueToInsert = slice.valueAt(currentIndex);
        int insertionIndex = findInsertionIndex(slice, currentIndex, valueToInsert);

        slice.shiftRight(insertionIndex, currentIndex);
        slice.setValueAt(insertionIndex, valueToInsert);
    }

    private static int findInsertionIndex(IntArraySlice slice, int currentIndex, int valueToInsert) {
        int scanIndex = currentIndex - 1;

        while (scanIndex >= slice.startInclusive() && slice.valueAt(scanIndex) > valueToInsert) {
            scanIndex--;
        }

        return scanIndex + 1;
    }
}
