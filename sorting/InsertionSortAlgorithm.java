package sorting;

final class InsertionSortAlgorithm implements IntArraySortAlgorithm {

    static final InsertionSortAlgorithm INSTANCE = new InsertionSortAlgorithm();

    private InsertionSortAlgorithm() {
        // Utility class.
    }

    @Override
    public void sort(IntArraySlice slice) {
        if (slice.hasFewerThanTwoElements()) {
            return;
        }

        for (int currentIndex = slice.startInclusive() + 1;
                currentIndex < slice.endExclusive();
                currentIndex++) {
            insertValue(slice, currentIndex);
        }
    }

    @Override
    public boolean isSorted(IntArraySlice slice) {
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
