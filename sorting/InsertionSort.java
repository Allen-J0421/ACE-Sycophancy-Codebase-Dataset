package sorting;

public final class InsertionSort {

    private InsertionSort() {
        // Utility class.
    }

    public static void sort(int[] values) {
        IntArraySlice.entireArray(values).sortWithInsertion();
    }

    public static void sortRange(int[] values, int startInclusive, int endExclusive) {
        IntArraySlice.of(values, startInclusive, endExclusive).sortWithInsertion();
    }

    public static boolean isSorted(int[] values) {
        return IntArraySlice.entireArray(values).isSorted();
    }

    public static boolean isSortedRange(int[] values, int startInclusive, int endExclusive) {
        return IntArraySlice.of(values, startInclusive, endExclusive).isSorted();
    }

    public static int[] sortedCopy(int[] values) {
        return IntArraySlice.entireArray(values).sortedCopy();
    }
}
