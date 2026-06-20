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

        buildMaxHeap(values, fromIndex, toIndex);

        for (int end = toIndex - 1; end > fromIndex; end--) {
            swap(values, fromIndex, end);
            siftDown(values, fromIndex, end, fromIndex);
        }
    }

    /**
     * Backward-compatible alias for {@link #sort(int[])}.
     *
     * @param values array to sort
     */
    @Deprecated
    public static void heapSort(int[] values) {
        sort(values);
    }

    private static void buildMaxHeap(int[] values, int fromIndex, int toIndex) {
        int length = toIndex - fromIndex;

        for (int root = fromIndex + length / 2 - 1; root >= fromIndex; root--) {
            siftDown(values, root, toIndex, fromIndex);
        }
    }

    private static void siftDown(int[] values, int rootIndex, int heapEndExclusive, int baseIndex) {
        int current = rootIndex;

        while (true) {
            int leftChild = baseIndex + ((current - baseIndex) * 2) + 1;
            if (leftChild >= heapEndExclusive) {
                return;
            }

            int rightChild = leftChild + 1;
            int largestChild = leftChild;

            if (rightChild < heapEndExclusive && values[rightChild] > values[leftChild]) {
                largestChild = rightChild;
            }

            if (values[current] >= values[largestChild]) {
                return;
            }

            swap(values, current, largestChild);
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
