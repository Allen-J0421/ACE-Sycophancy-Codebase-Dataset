final class Partitioner {

    private final SelectionContext context;
    private final PivotSelector pivotSelector;

    Partitioner(SelectionContext context, PivotSelector pivotSelector) {
        this.context = context;
        this.pivotSelector = pivotSelector;
    }

    int partition() {
        SearchBounds searchBounds = context.searchBounds();
        int[] values = context.values();

        movePivotToEnd(searchBounds, values);
        int pivot = values[searchBounds.high()];
        int pivotIndex = searchBounds.low();

        for (int i = searchBounds.low(); i < searchBounds.high(); i++) {
            if (values[i] < pivot) {
                swap(values, i, pivotIndex);
                pivotIndex++;
            }
        }

        swap(values, searchBounds.high(), pivotIndex);
        return pivotIndex;
    }

    private void movePivotToEnd(SearchBounds searchBounds, int[] values) {
        swap(
            values,
            pivotSelector.selectPivotIndex(values, searchBounds.low(), searchBounds.high()),
            searchBounds.high()
        );
    }

    private void swap(int[] values, int left, int right) {
        if (left == right) {
            return;
        }

        int temp = values[left];
        values[left] = values[right];
        values[right] = temp;
    }
}
