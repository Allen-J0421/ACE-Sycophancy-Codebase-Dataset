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

        if (values.length < 2) {
            return;
        }

        buildMaxHeap(values);

        for (int end = values.length - 1; end > 0; end--) {
            swap(values, 0, end);
            siftDown(values, 0, end);
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

    private static void buildMaxHeap(int[] values) {
        for (int root = values.length / 2 - 1; root >= 0; root--) {
            siftDown(values, root, values.length);
        }
    }

    private static void siftDown(int[] values, int rootIndex, int heapSize) {
        int current = rootIndex;

        while (true) {
            int leftChild = 2 * current + 1;
            if (leftChild >= heapSize) {
                return;
            }

            int rightChild = leftChild + 1;
            int largestChild = leftChild;

            if (rightChild < heapSize && values[rightChild] > values[leftChild]) {
                largestChild = rightChild;
            }

            if (values[current] >= values[largestChild]) {
                return;
            }

            swap(values, current, largestChild);
            current = largestChild;
        }
    }

    private static void swap(int[] values, int firstIndex, int secondIndex) {
        int temp = values[firstIndex];
        values[firstIndex] = values[secondIndex];
        values[secondIndex] = temp;
    }
}
