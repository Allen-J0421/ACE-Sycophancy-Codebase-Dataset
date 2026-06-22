package sorting;

public final class InsertionSort {

    private static final IntArraySortAlgorithm ALGORITHM = InsertionSortAlgorithm.INSTANCE;

    private InsertionSort() {
        // Utility class.
    }

    public static void sort(int[] values) {
        IntArraySortSupport.sort(ALGORITHM, values);
    }

    public static void sortRange(int[] values, int startInclusive, int endExclusive) {
        IntArraySortSupport.sortRange(ALGORITHM, values, startInclusive, endExclusive);
    }

    public static boolean isSorted(int[] values) {
        return IntArraySortSupport.isSorted(ALGORITHM, values);
    }

    public static boolean isSortedRange(int[] values, int startInclusive, int endExclusive) {
        return IntArraySortSupport.isSortedRange(ALGORITHM, values, startInclusive, endExclusive);
    }

    public static int[] sortedCopy(int[] values) {
        return IntArraySortSupport.sortedCopy(ALGORITHM, values);
    }
}
