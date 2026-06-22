final class SelectionContext {

    private final int[] values;
    private final int targetIndex;
    private final SearchBounds searchBounds;

    SelectionContext(int[] values, int targetIndex) {
        this.values = values;
        this.targetIndex = targetIndex;
        this.searchBounds = SearchBounds.initial(values.length);
    }

    int[] values() {
        return values;
    }

    SearchBounds searchBounds() {
        return searchBounds;
    }

    boolean isTargetIndex(int index) {
        return index == targetIndex;
    }

    int valueAt(int index) {
        return values[index];
    }

    void narrowSearchBounds(int partitionIndex) {
        searchBounds.narrowToward(targetIndex, partitionIndex);
    }
}
