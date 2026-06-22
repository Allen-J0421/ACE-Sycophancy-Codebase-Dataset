public final class QuickSelect {

    private QuickSelect() {
        // Utility class.
    }

    private static void validateSelectionRequest(int[] values, int k) {
        if (values == null) {
            throw new IllegalArgumentException("values must not be null");
        }
        if (values.length == 0) {
            throw new IllegalArgumentException("values must not be empty");
        }
        if (k < 1 || k > values.length) {
            throw new IllegalArgumentException("k must be between 1 and values.length");
        }
    }

    /**
     * Returns the k-th smallest element without mutating the caller's array.
     */
    public static int selectKthSmallest(int[] values, int k) {
        validateSelectionRequest(values, k);
        return selectByTargetIndex(values.clone(), k - 1);
    }

    /**
     * Returns the k-th smallest element and may reorder the provided array.
     */
    public static int selectKthSmallestInPlace(int[] values, int k) {
        validateSelectionRequest(values, k);
        return selectByTargetIndex(values, k - 1);
    }

    private static int selectByTargetIndex(int[] values, int targetIndex) {
        int low = 0;
        int high = values.length - 1;

        while (low <= high) {
            int partitionIndex = partition(values, low, high);

            if (partitionIndex == targetIndex) {
                return values[partitionIndex];
            }

            if (partitionIndex < targetIndex) {
                low = partitionIndex + 1;
            } else {
                high = partitionIndex - 1;
            }
        }

        throw new IllegalStateException("Quickselect failed to find the requested element");
    }

    private static int partition(int[] values, int low, int high) {
        int selectedPivotIndex = choosePivotIndex(values, low, high);
        swap(values, selectedPivotIndex, high);

        int pivotValue = values[high];
        int storeIndex = low;

        for (int i = low; i < high; i++) {
            if (values[i] < pivotValue) {
                swap(values, i, storeIndex);
                storeIndex++;
            }
        }

        swap(values, storeIndex, high);
        return storeIndex;
    }

    private static int choosePivotIndex(int[] values, int low, int high) {
        int mid = low + ((high - low) / 2);

        int lowValue = values[low];
        int midValue = values[mid];
        int highValue = values[high];

        if ((lowValue <= midValue && midValue <= highValue) || (highValue <= midValue && midValue <= lowValue)) {
            return mid;
        }
        if ((midValue <= lowValue && lowValue <= highValue) || (highValue <= lowValue && lowValue <= midValue)) {
            return low;
        }
        return high;
    }

    private static void swap(int[] values, int i, int j) {
        int temp = values[i];
        values[i] = values[j];
        values[j] = temp;
    }
}
