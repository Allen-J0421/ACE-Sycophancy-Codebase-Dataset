package sorting;

public final class InsertionSort {

    private InsertionSort() {
        // Utility class.
    }

    public static void sort(int[] values) {
        InsertionSortAlgorithm.sort(IntArraySlice.entireArray(values));
    }

    public static void sortRange(int[] values, int startInclusive, int endExclusive) {
        InsertionSortAlgorithm.sort(IntArraySlice.of(values, startInclusive, endExclusive));
    }

    public static boolean isSorted(int[] values) {
        return InsertionSortAlgorithm.isSorted(IntArraySlice.entireArray(values));
    }

    public static boolean isSortedRange(int[] values, int startInclusive, int endExclusive) {
        return InsertionSortAlgorithm.isSorted(IntArraySlice.of(values, startInclusive, endExclusive));
    }

    public static int[] sortedCopy(int[] values) {
        IntArraySlice copy = IntArraySlice.entireArray(values).copy();
        InsertionSortAlgorithm.sort(copy);
        return copy.values();
    }
}
