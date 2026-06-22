import java.util.Arrays;

final class QuickSelect {

    private QuickSelect() {
    }

    public static int kthSmallest(int[] values, int k) {
        if (values == null) {
            throw new IllegalArgumentException("Array must not be null");
        }

        return kthSmallest(values, 0, values.length - 1, k);
    }

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
        if (values == null) {
            throw new IllegalArgumentException("Array must not be null");
        }

        if (values.length == 0) {
            throw new IllegalArgumentException("Array must not be empty");
        }

        if (low < 0 || high >= values.length || low > high) {
            throw new IllegalArgumentException("Invalid search bounds");
        }
    }

    private static void swap(int[] values, int firstIndex, int secondIndex) {
        int temp = values[firstIndex];
        values[firstIndex] = values[secondIndex];
        values[secondIndex] = temp;
    }

    public static void main(String[] args) {
        int[] array = new int[] { 10, 4, 5, 8, 6, 11, 26 };
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
