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
    public static int kthSmallest(int[] values, int position) {
        validateArray(values);
        return kthSmallest(values, 0, values.length - 1, position);
    }

    /**
     * Returns the k-th smallest value within the inclusive bounds.
     *
     * <p>{@code position} is one-based and refers to the array position,
     * matching the original API contract.
     */
    public static int kthSmallest(
            int[] values, int startIndex, int endIndex, int position) {
        validateSelectionBounds(values, startIndex, endIndex, position);

        int targetIndex = position - 1;
        int left = startIndex;
        int right = endIndex;

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
    public static int partition(int[] values, int startIndex, int endIndex) {
        validatePartitionBounds(values, startIndex, endIndex);
        return partitionUnchecked(values, startIndex, endIndex);
    }

    private static int partitionUnchecked(int[] values, int startIndex, int endIndex) {
        int pivotValue = values[endIndex];
        int pivotIndex = startIndex;

        for (int i = startIndex; i < endIndex; i++) {
            if (values[i] < pivotValue) {
                swap(values, i, pivotIndex);
                pivotIndex++;
            }
        }

        swap(values, pivotIndex, endIndex);
        return pivotIndex;
    }

    private static void validateSelectionBounds(
            int[] values, int startIndex, int endIndex, int position) {
        validatePartitionBounds(values, startIndex, endIndex);

        if (position <= startIndex || position > endIndex + 1) {
            throw new IllegalArgumentException("Position is outside the search bounds");
        }
    }

    private static void validatePartitionBounds(
            int[] values, int startIndex, int endIndex) {
        validateArray(values);

        if (startIndex < 0 || endIndex >= values.length || startIndex > endIndex) {
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
        QuickSelectDemo.main(args);
    }
}

final class QuickSelectDemo {
    private static final int SAMPLE_POSITION = 3;
    private static final String OUT_OF_BOUNDS_MESSAGE = "Index out of bound";
    private static final String RESULT_PREFIX = "K-th smallest element in array : ";

    private QuickSelectDemo() {
    }

    public static void main(String[] args) {
        printKthSmallest(sampleValues(), SAMPLE_POSITION);
    }

    private static void printKthSmallest(int[] values, int position) {
        if (!isValidKPosition(values, position)) {
            System.out.println(OUT_OF_BOUNDS_MESSAGE);
        } else {
            int[] workingCopy = Arrays.copyOf(values, values.length);
            System.out.println(
                RESULT_PREFIX + QuickSelect.kthSmallest(workingCopy, position));
        }
    }

    private static boolean isValidKPosition(int[] values, int position) {
        return values != null && position >= 1 && position <= values.length;
    }

    private static int[] sampleValues() {
        return new int[] { 10, 4, 5, 8, 6, 11, 26 };
    }
}
