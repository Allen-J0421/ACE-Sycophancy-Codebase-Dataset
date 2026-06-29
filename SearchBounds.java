final class SearchBounds {
    private int lowerIndex;
    private int upperIndex;

    private SearchBounds(int lowerIndex, int upperIndex) {
        this.lowerIndex = lowerIndex;
        this.upperIndex = upperIndex;
    }

    static SearchBounds forLength(int length) {
        return new SearchBounds(0, length - 1);
    }

    boolean hasCandidates() {
        return lowerIndex <= upperIndex;
    }

    int middleIndex() {
        return lowerIndex + (upperIndex - lowerIndex) / 2;
    }

    void discardLowerHalfThrough(int index) {
        lowerIndex = index + 1;
    }

    void discardUpperHalfFrom(int index) {
        upperIndex = index - 1;
    }
}
