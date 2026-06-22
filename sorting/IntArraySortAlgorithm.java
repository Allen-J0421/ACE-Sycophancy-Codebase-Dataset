package sorting;

interface IntArraySortAlgorithm {

    void sort(IntArraySlice slice);

    boolean isSorted(IntArraySlice slice);

    default void sort(int[] values) {
        sort(IntArraySlice.entireArray(values));
    }

    default void sort(int[] values, int startInclusive, int endExclusive) {
        sort(IntArraySlice.of(values, startInclusive, endExclusive));
    }

    default boolean isSorted(int[] values) {
        return isSorted(IntArraySlice.entireArray(values));
    }

    default boolean isSorted(int[] values, int startInclusive, int endExclusive) {
        return isSorted(IntArraySlice.of(values, startInclusive, endExclusive));
    }

    default int[] sortedCopy(int[] values) {
        IntArraySlice copy = IntArraySlice.entireArray(values).copy();
        sort(copy);
        return copy.values();
    }
}
