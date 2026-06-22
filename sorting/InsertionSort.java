package sorting;

public final class InsertionSort {

    private static final IntArraySortAlgorithm ALGORITHM = InsertionSortAlgorithm.INSTANCE;

    private InsertionSort() {
        // Utility class.
    }

    public static void sort(int[] values) {
        ALGORITHM.sort(values);
    }

    public static void sortRange(int[] values, int startInclusive, int endExclusive) {
        ALGORITHM.sort(values, startInclusive, endExclusive);
    }

    public static boolean isSorted(int[] values) {
        return ALGORITHM.isSorted(values);
    }

    public static boolean isSortedRange(int[] values, int startInclusive, int endExclusive) {
        return ALGORITHM.isSorted(values, startInclusive, endExclusive);
    }

    public static int[] sortedCopy(int[] values) {
        return ALGORITHM.sortedCopy(values);
    }
}
