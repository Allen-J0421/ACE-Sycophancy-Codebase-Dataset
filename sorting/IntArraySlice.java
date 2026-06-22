package sorting;

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

    IntArraySlice copy() {
        return new IntArraySlice(values.clone(), range);
    }

    int[] values() {
        return values;
    }

    int startInclusive() {
        return range.startInclusive();
    }

    int endExclusive() {
        return range.endExclusive();
    }

    boolean hasFewerThanTwoElements() {
        return range.hasFewerThanTwoElements();
    }

    int valueAt(int index) {
        return values[index];
    }

    void setValueAt(int index, int value) {
        values[index] = value;
    }

    void shiftRight(int startIndex, int endIndex) {
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
