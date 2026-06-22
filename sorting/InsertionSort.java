package sorting;

public final class InsertionSort {

    private InsertionSort() {
        // Utility class.
    }

    public static void sort(int[] values) {
        InsertionSortAlgorithm.sort(values);
    }

    public static void sortRange(int[] values, int startInclusive, int endExclusive) {
        InsertionSortAlgorithm.sort(values, startInclusive, endExclusive);
    }

    public static boolean isSorted(int[] values) {
        return InsertionSortAlgorithm.isSorted(values);
    }

    public static boolean isSortedRange(int[] values, int startInclusive, int endExclusive) {
        return InsertionSortAlgorithm.isSorted(values, startInclusive, endExclusive);
    }

    public static int[] sortedCopy(int[] values) {
        return InsertionSortAlgorithm.sortedCopy(values);
    }
}
