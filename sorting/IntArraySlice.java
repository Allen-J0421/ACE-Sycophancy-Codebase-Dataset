package sorting;

record IntArraySlice(int[] values, int startInclusive, int endExclusive) {

    IntArraySlice {
        requireArray(values);
        requireRange(values.length, startInclusive, endExclusive);
    }

    static IntArraySlice entireArray(int[] values) {
        requireArray(values);
        return new IntArraySlice(values, 0, values.length);
    }

    static IntArraySlice of(int[] values, int startInclusive, int endExclusive) {
        requireArray(values);
        return new IntArraySlice(values, startInclusive, endExclusive);
    }

    IntArraySlice copy() {
        return new IntArraySlice(values.clone(), startInclusive, endExclusive);
    }

    boolean hasFewerThanTwoElements() {
        return endExclusive - startInclusive < 2;
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

    private static void requireRange(int length, int startInclusive, int endExclusive) {
        if (startInclusive < 0 || endExclusive < startInclusive || endExclusive > length) {
            throw new IllegalArgumentException(
                    "invalid range: [" + startInclusive + ", " + endExclusive + ") for length " + length);
        }
    }
}
