import java.util.Arrays;

final class QuickSelect {

    private QuickSelect() {
        // Utility class.
    }

    public static int selectKthSmallest(int[] values, int rank) {
        validateRank(values, rank);
        return selectKthSmallestInPlace(Arrays.copyOf(values, values.length), rank);
    }

    public static int selectKthSmallestInPlace(int[] values, int rank) {
        validateRank(values, rank);
        return quickSelect(values, rank - 1);
    }

    public static int kthSmallest(int[] values, int k) {
        return selectKthSmallest(values, k);
    }

    private static int partition(int[] values, int low, int high) {
        moveMedianPivotToEnd(values, low, high);
        int pivot = values[high];
        int pivotIndex = low;

        for (int i = low; i < high; i++) {
            if (values[i] < pivot) {
                swap(values, i, pivotIndex);
                pivotIndex++;
            }
        }

        swap(values, high, pivotIndex);
        return pivotIndex;
    }

    private static void moveMedianPivotToEnd(int[] values, int low, int high) {
        swap(values, medianOfThreeIndex(values, low, high), high);
    }

    private static int medianOfThreeIndex(int[] values, int low, int high) {
        int mid = low + (high - low) / 2;

        if (values[low] > values[mid]) {
            swap(values, low, mid);
        }

        if (values[low] > values[high]) {
            swap(values, low, high);
        }

        if (values[mid] > values[high]) {
            swap(values, mid, high);
        }

        return mid;
    }

    private static void swap(int[] values, int left, int right) {
        if (left == right) {
            return;
        }

        int temp = values[left];
        values[left] = values[right];
        values[right] = temp;
    }

    private static int quickSelect(int[] values, int targetIndex) {
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

        throw new IllegalStateException("Quickselect failed to locate the target index");
    }

    private static void validateRank(int[] values, int rank) {
        if (values == null) {
            throw new IllegalArgumentException("Input array must not be null");
        }

        if (values.length == 0) {
            throw new IllegalArgumentException("Input array must not be empty");
        }

        if (rank < 1 || rank > values.length) {
            throw new IllegalArgumentException(
                "rank must be between 1 and " + values.length + " inclusive");
        }
    }

    public static void main(String[] args) {
        QuickSelectDemo.main(args);
    }
}

final class QuickSelectDemo {

    private QuickSelectDemo() {
        // Utility class.
    }

    public static void main(String[] args) {
        int[] values = { 10, 4, 5, 8, 6, 11, 26 };
        int rank = 3;

        System.out.println(
            "K-th smallest element in array: "
            + QuickSelect.selectKthSmallest(values, rank));
    }
}
