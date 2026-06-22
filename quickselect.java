public final class QuickSelect {

    private QuickSelect() {
        // Utility class.
    }

    private static void swap(int[] values, int i, int j) {
        int temp = values[i];
        values[i] = values[j];
        values[j] = temp;
    }

    private static int partition(int[] values, int low, int high) {
        int pivotValue = values[high];
        int pivotIndex = low;

        for (int i = low; i < high; i++) {
            if (values[i] < pivotValue) {
                swap(values, i, pivotIndex);
                pivotIndex++;
            }
        }

        swap(values, pivotIndex, high);
        return pivotIndex;
    }

    public static int selectKthSmallest(int[] values, int k) {
        if (values == null) {
            throw new IllegalArgumentException("values must not be null");
        }
        if (values.length == 0) {
            throw new IllegalArgumentException("values must not be empty");
        }
        if (k < 1 || k > values.length) {
            throw new IllegalArgumentException("k must be between 1 and values.length");
        }

        int low = 0;
        int high = values.length - 1;
        int targetIndex = k - 1;

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

    public static void main(String[] args) {
        int[] values = { 10, 4, 5, 8, 6, 11, 26 };
        int kPosition = 3;

        try {
            int result = selectKthSmallest(values.clone(), kPosition);
            System.out.println("K-th smallest element in array : " + result);
        } catch (IllegalArgumentException exception) {
            System.out.println("Index out of bound");
        }
    }
}
