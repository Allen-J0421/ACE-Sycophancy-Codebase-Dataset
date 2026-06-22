final class SearchBounds {

    private int low;
    private int high;

    private SearchBounds(int low, int high) {
        this.low = low;
        this.high = high;
    }

    static SearchBounds initial(int length) {
        return new SearchBounds(0, length - 1);
    }

    boolean isActive() {
        return low <= high;
    }

    int low() {
        return low;
    }

    int high() {
        return high;
    }

    void narrowToward(int targetIndex, int partitionIndex) {
        if (partitionIndex < targetIndex) {
            low = partitionIndex + 1;
        } else {
            high = partitionIndex - 1;
        }
    }
}
