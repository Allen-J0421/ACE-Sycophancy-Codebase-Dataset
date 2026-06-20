import java.util.Arrays;
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
        Objects.checkFromToIndex(fromIndex, toIndex, values.length);

        if (toIndex - fromIndex < 2) {
            return;
        }

        new HeapSlice(values, fromIndex, toIndex).sort();
    }

    /**
     * Returns a sorted copy of the supplied array.
     *
     * @param values array to copy and sort
     * @return a new sorted array
     * @throws NullPointerException if {@code values} is null
     */
    public static int[] sortedCopy(int[] values) {
        Objects.requireNonNull(values, "values");

        int[] copy = Arrays.copyOf(values, values.length);
        sort(copy);
        return copy;
    }

    private static final class HeapSlice {
        private final int[] values;
        private final int baseIndex;
        private final int heapSize;

        private HeapSlice(int[] values, int baseIndex, int toIndex) {
            this.values = values;
            this.baseIndex = baseIndex;
            this.heapSize = toIndex - baseIndex;
        }

        private void sort() {
            buildMaxHeap();

            for (int end = heapSize - 1; end > 0; end--) {
                swap(0, end);
                siftDown(end, 0);
            }
        }

        private void buildMaxHeap() {
            for (int root = heapSize / 2 - 1; root >= 0; root--) {
                siftDown(heapSize, root);
            }
        }

        private void siftDown(int heapLimit, int rootIndex) {
            int current = rootIndex;

            while (true) {
                int leftChild = 2 * current + 1;
                if (leftChild >= heapLimit) {
                    return;
                }

                int rightChild = leftChild + 1;
                int largestChild = leftChild;

                if (rightChild < heapLimit && valueAt(rightChild) > valueAt(leftChild)) {
                    largestChild = rightChild;
                }

                if (valueAt(current) >= valueAt(largestChild)) {
                    return;
                }

                swap(current, largestChild);
                current = largestChild;
            }
        }

        private int valueAt(int relativeIndex) {
            return values[baseIndex + relativeIndex];
        }

        private void swap(int firstRelativeIndex, int secondRelativeIndex) {
            int firstIndex = baseIndex + firstRelativeIndex;
            int secondIndex = baseIndex + secondRelativeIndex;
            int temp = values[firstIndex];
            values[firstIndex] = values[secondIndex];
            values[secondIndex] = temp;
        }
    }
}
