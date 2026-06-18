final class IndexRange {

    final int fromIndex;
    final int toIndex;

    private IndexRange(int fromIndex, int toIndex) {
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }

    static IndexRange of(int length, int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex > length) {
            throw new IndexOutOfBoundsException(
                    "Range [" + fromIndex + ", " + toIndex + ") out of bounds for length " + length);
        }

        if (fromIndex > toIndex) {
            throw new IllegalArgumentException(
                    "fromIndex (" + fromIndex + ") must be <= toIndex (" + toIndex + ")");
        }

        return new IndexRange(fromIndex, toIndex);
    }

    int length() {
        return toIndex - fromIndex;
    }
}
