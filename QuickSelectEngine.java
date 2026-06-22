final class QuickSelectEngine {

    private final int[] values;
    private final int targetIndex;
    private final PivotSelector pivotSelector;
    private int low;
    private int high;

    QuickSelectEngine(int[] values, int targetIndex, PivotSelector pivotSelector) {
        this.values = values;
        this.targetIndex = targetIndex;
        this.pivotSelector = pivotSelector;
        this.low = 0;
        this.high = values.length - 1;
    }

    int select() {
        while (low <= high) {
            int partitionIndex = partitionCurrentRange();

            if (partitionIndex == targetIndex) {
                return values[partitionIndex];
            }

            if (partitionIndex < targetIndex) {
                low = partitionIndex + 1;
            } else {
                high = partitionIndex - 1;
            }
        }

        throw new IllegalStateException("Quickselect failed to locate the target index");
    }

    private int partitionCurrentRange() {
        movePivotToEnd();
        int pivot = values[high];
        int pivotIndex = low;

        for (int i = low; i < high; i++) {
            if (values[i] < pivot) {
                swap(i, pivotIndex);
                pivotIndex++;
            }
        }

        swap(high, pivotIndex);
        return pivotIndex;
    }

    private void movePivotToEnd() {
        swap(pivotSelector.selectPivotIndex(values, low, high), high);
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
