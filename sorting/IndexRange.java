package sorting;

final class IndexRange {

    private final int startInclusive;
    private final int endExclusive;

    private IndexRange(int startInclusive, int endExclusive) {
        this.startInclusive = startInclusive;
        this.endExclusive = endExclusive;
    }

    static IndexRange forArray(int length) {
        return of(length, 0, length);
    }

    static IndexRange of(int length, int startInclusive, int endExclusive) {
        if (startInclusive < 0 || endExclusive < startInclusive || endExclusive > length) {
            throw new IllegalArgumentException(
                    "invalid range: [" + startInclusive + ", " + endExclusive + ") for length " + length);
        }

        return new IndexRange(startInclusive, endExclusive);
    }

    int startInclusive() {
        return startInclusive;
    }

    int endExclusive() {
        return endExclusive;
    }

    boolean hasFewerThanTwoElements() {
        return endExclusive - startInclusive < 2;
    }
}
