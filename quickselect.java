import java.util.Arrays;

final class QuickSelect {

    private QuickSelect() {
        // Utility class.
    }

    private static int partition(int[] values, int low, int high) {
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

    private static void swap(int[] values, int left, int right) {
        int temp = values[left];
        values[left] = values[right];
        values[right] = temp;
    }

    public static int kthSmallest(int[] values, int k) {
        validateInput(values, k);
        return quickSelect(Arrays.copyOf(values, values.length), k - 1);
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

    private static void validateInput(int[] values, int k) {
        if (values == null) {
            throw new IllegalArgumentException("Input array must not be null");
        }

        if (values.length == 0) {
            throw new IllegalArgumentException("Input array must not be empty");
        }

        if (k < 1 || k > values.length) {
            throw new IllegalArgumentException(
                "k must be between 1 and " + values.length + " inclusive");
        }
    }

    public static void main(String[] args) {
        int[] values = { 10, 4, 5, 8, 6, 11, 26 };
        int kPosition = 3;

        System.out.println(
            "K-th smallest element in array: "
            + kthSmallest(values, kPosition));
    }
}
