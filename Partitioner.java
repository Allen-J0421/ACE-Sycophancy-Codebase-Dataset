final class Partitioner {

    private final int[] values;
    private final PivotSelector pivotSelector;

    Partitioner(int[] values, PivotSelector pivotSelector) {
        this.values = values;
        this.pivotSelector = pivotSelector;
    }

    int partition(SearchBounds searchBounds) {
        movePivotToEnd(searchBounds);
        int pivot = values[searchBounds.high()];
        int pivotIndex = searchBounds.low();

        for (int i = searchBounds.low(); i < searchBounds.high(); i++) {
            if (values[i] < pivot) {
                swap(i, pivotIndex);
                pivotIndex++;
            }
        }

        swap(searchBounds.high(), pivotIndex);
        return pivotIndex;
    }

    private void movePivotToEnd(SearchBounds searchBounds) {
        swap(
            pivotSelector.selectPivotIndex(values, searchBounds.low(), searchBounds.high()),
            searchBounds.high()
        );
    }

    private void swap(int left, int right) {
        if (left == right) {
            return;
        }

        int temp = values[left];
        values[left] = values[right];
        values[right] = temp;
    }
}
