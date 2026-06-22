import java.util.Arrays;

/**
 * Utility methods for selecting ordered elements from an integer array.
 */
final class QuickSelect {

    private QuickSelect() {
    }

    /**
     * Returns the k-th smallest value in the whole array.
     *
     * <p>The input array is partitioned in place. Pass a copy when the original
     * ordering must be preserved.
     */
    public static int kthSmallest(int[] values, int k) {
        validateArray(values);
        return kthSmallest(values, 0, values.length - 1, k);
    }

    /**
     * Returns the k-th smallest value within the inclusive bounds.
     *
     * <p>{@code k} is one-based and refers to the array position, matching the
     * original API contract.
     */
    public static int kthSmallest(int[] values, int low, int high, int k) {
        validateRange(values, low, high, k);

        int targetIndex = k - 1;
        int left = low;
        int right = high;

        while (left <= right) {
            int pivotIndex = partitionUnchecked(values, left, right);

            if (pivotIndex == targetIndex) {
                return values[pivotIndex];
            }

            if (pivotIndex < targetIndex) {
                left = pivotIndex + 1;
            } else {
                right = pivotIndex - 1;
            }
        }

        throw new IllegalStateException("Unable to select the requested element");
    }

    /**
     * Partitions the inclusive range around the last value as pivot.
     */
    public static int partition(int[] values, int low, int high) {
        validatePartitionBounds(values, low, high);
        return partitionUnchecked(values, low, high);
    }

    private static int partitionUnchecked(int[] values, int low, int high) {
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

    private static void validateRange(int[] values, int low, int high, int k) {
        validatePartitionBounds(values, low, high);

        if (k <= low || k > high + 1) {
            throw new IllegalArgumentException("k is outside the search bounds");
        }
    }

    private static void validatePartitionBounds(int[] values, int low, int high) {
        validateArray(values);

        if (low < 0 || high >= values.length || low > high) {
            throw new IllegalArgumentException("Invalid search bounds");
        }
    }

    private static void validateArray(int[] values) {
        if (values == null) {
            throw new IllegalArgumentException("Array must not be null");
        }

        if (values.length == 0) {
            throw new IllegalArgumentException("Array must not be empty");
        }
    }

    private static void swap(int[] values, int firstIndex, int secondIndex) {
        int temp = values[firstIndex];
        values[firstIndex] = values[secondIndex];
        values[secondIndex] = temp;
    }

    public static void main(String[] args) {
        int[] array = { 10, 4, 5, 8, 6, 11, 26 };
        int kPosition = 3;

        if (kPosition < 1 || kPosition > array.length) {
            System.out.println("Index out of bound");
        } else {
            int[] workingCopy = Arrays.copyOf(array, array.length);
            System.out.println(
                "K-th smallest element in array : "
                + kthSmallest(workingCopy, kPosition));
        }
    }
}
