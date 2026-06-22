package sorting;

final class IntArraySortSupport {

    private IntArraySortSupport() {
        // Utility class.
    }

    static void sort(IntArraySortAlgorithm algorithm, int[] values) {
        algorithm.sort(IntArraySlice.entireArray(values));
    }

    static void sortRange(
            IntArraySortAlgorithm algorithm,
            int[] values,
            int startInclusive,
            int endExclusive) {
        algorithm.sort(IntArraySlice.of(values, startInclusive, endExclusive));
    }

    static boolean isSorted(IntArraySortAlgorithm algorithm, int[] values) {
        return algorithm.isSorted(IntArraySlice.entireArray(values));
    }

    static boolean isSortedRange(
            IntArraySortAlgorithm algorithm,
            int[] values,
            int startInclusive,
            int endExclusive) {
        return algorithm.isSorted(IntArraySlice.of(values, startInclusive, endExclusive));
    }

    static int[] sortedCopy(IntArraySortAlgorithm algorithm, int[] values) {
        IntArraySlice copy = IntArraySlice.entireArray(values).copy();
        algorithm.sort(copy);
        return copy.values();
    }
}
