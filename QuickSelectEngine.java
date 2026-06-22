final class QuickSelectEngine {

    private final int[] values;
    private final int targetIndex;
    private final PivotSelector pivotSelector;
    private final SearchBounds searchBounds;

    QuickSelectEngine(int[] values, int targetIndex, PivotSelector pivotSelector) {
        this.values = values;
        this.targetIndex = targetIndex;
        this.pivotSelector = pivotSelector;
        this.searchBounds = SearchBounds.initial(values.length);
    }

    static int select(int[] values, int targetIndex) {
        return new QuickSelectEngine(values, targetIndex, MedianOfThreePivotSelector.INSTANCE)
            .select();
    }

    int select() {
        while (searchBounds.isActive()) {
            int partitionIndex = partitionCurrentRange();

            if (partitionIndex == targetIndex) {
                return values[partitionIndex];
            }

            searchBounds.narrowToward(targetIndex, partitionIndex);
        }

        throw new IllegalStateException("Quickselect failed to locate the target index");
    }

    private int partitionCurrentRange() {
        movePivotToEnd();
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

    private void movePivotToEnd() {
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

    private static final class SearchBounds {

        private int low;
        private int high;

        private SearchBounds(int low, int high) {
            this.low = low;
            this.high = high;
        }

        private static SearchBounds initial(int length) {
            return new SearchBounds(0, length - 1);
        }

        private boolean isActive() {
            return low <= high;
        }

        private int low() {
            return low;
        }

        private int high() {
            return high;
        }

        private void narrowToward(int targetIndex, int partitionIndex) {
            if (partitionIndex < targetIndex) {
                low = partitionIndex + 1;
            } else {
                high = partitionIndex - 1;
            }
        }
    }
}

interface PivotSelector {

    int selectPivotIndex(int[] values, int low, int high);
}

enum MedianOfThreePivotSelector implements PivotSelector {
    INSTANCE;

    @Override
    public int selectPivotIndex(int[] values, int low, int high) {
        int mid = low + (high - low) / 2;

        order(values, low, mid);
        order(values, low, high);
        order(values, mid, high);

        return mid;
    }

    private void order(int[] values, int left, int right) {
        if (values[left] > values[right]) {
            int temp = values[left];
            values[left] = values[right];
            values[right] = temp;
        }
    }
}
