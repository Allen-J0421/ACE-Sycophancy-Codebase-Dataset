import java.util.Objects;

/**
 * In-place ascending sort for integer arrays using heap sort.
 */
public final class HeapSort {

    private HeapSort() {
        // Utility class; do not instantiate.
    }

    /**
     * Sorts the supplied array in ascending order.
     *
     * @param values array to sort
     * @throws NullPointerException if {@code values} is null
     */
    public static void sort(int[] values) {
        Objects.requireNonNull(values, "values");
        sort(values, 0, values.length);
    }

    /**
     * Sorts the specified range of the supplied array in ascending order.
     * The range is {@code [fromIndex, toIndex)}.
     *
     * @param values array to sort
     * @param fromIndex inclusive start of the range
     * @param toIndex exclusive end of the range
     * @throws NullPointerException if {@code values} is null
     * @throws IndexOutOfBoundsException if the range is invalid
     */
    public static void sort(int[] values, int fromIndex, int toIndex) {
        Objects.requireNonNull(values, "values");
        validateRange(values.length, fromIndex, toIndex);

        if (toIndex - fromIndex < 2) {
            return;
        }

        int heapSize = toIndex - fromIndex;
        buildMaxHeap(values, fromIndex, heapSize);

        for (int end = toIndex - 1; end > fromIndex; end--) {
            swap(values, fromIndex, end);
            siftDown(values, fromIndex, end - fromIndex, 0);
        }
    }

    private static void buildMaxHeap(int[] values, int baseIndex, int heapSize) {
        for (int root = heapSize / 2 - 1; root >= 0; root--) {
            siftDown(values, baseIndex, heapSize, root);
        }
    }

    private static void siftDown(int[] values, int baseIndex, int heapSize, int rootIndex) {
        int current = rootIndex;

        while (true) {
            int leftChild = 2 * current + 1;
            if (leftChild >= heapSize) {
                return;
            }

            int rightChild = leftChild + 1;
            int largestChild = leftChild;

            if (rightChild < heapSize
                    && values[baseIndex + rightChild] > values[baseIndex + leftChild]) {
                largestChild = rightChild;
            }

            if (values[baseIndex + current] >= values[baseIndex + largestChild]) {
                return;
            }

            swap(values, baseIndex + current, baseIndex + largestChild);
            current = largestChild;
        }
    }

    private static void validateRange(int length, int fromIndex, int toIndex) {
        if (fromIndex < 0) {
            throw new IndexOutOfBoundsException("fromIndex (" + fromIndex + ") must be non-negative");
        }

        if (toIndex > length) {
            throw new IndexOutOfBoundsException(
                    "toIndex (" + toIndex + ") must not exceed array length (" + length + ")");
        }

        if (fromIndex > toIndex) {
            throw new IndexOutOfBoundsException(
                    "fromIndex (" + fromIndex + ") must not be greater than toIndex (" + toIndex + ")");
        }
    }

    private static void swap(int[] values, int firstIndex, int secondIndex) {
        int temp = values[firstIndex];
        values[firstIndex] = values[secondIndex];
        values[secondIndex] = temp;
    }
}
