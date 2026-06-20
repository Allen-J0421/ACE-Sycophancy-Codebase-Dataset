import java.util.Objects;

/**
 * In-place heap sort for integer arrays.
 */
public final class HeapSort {
    private HeapSort() {
    }

    /**
     * Sorts {@code values} in ascending order.
     *
     * @param values the array to sort
     * @throws NullPointerException if {@code values} is null
     */
    public static void sort(int[] values) {
        Objects.requireNonNull(values, "values");

        if (values.length < 2) {
            return;
        }

        MaxHeap.sort(values);
    }

    /**
     * Compatibility wrapper for callers that use the original method name.
     *
     * @param values the array to sort
     * @throws NullPointerException if {@code values} is null
     */
    public static void heapSort(int[] values) {
        sort(values);
    }

    private static final class MaxHeap {
        private static final int ROOT_INDEX = 0;

        private final int[] values;
        private int heapSize;

        private MaxHeap(int[] values) {
            this.values = values;
            heapSize = values.length;
        }

        private static void sort(int[] values) {
            new MaxHeap(values).sortAscending();
        }

        private void sortAscending() {
            build();

            while (heapSize > 1) {
                moveMaxToEnd();
                siftDown(ROOT_INDEX);
            }
        }

        private void build() {
            for (int index = firstParentIndex(); index >= 0; index--) {
                siftDown(index);
            }
        }

        private int firstParentIndex() {
            return heapSize / 2 - 1;
        }

        private void moveMaxToEnd() {
            heapSize--;
            swap(ROOT_INDEX, heapSize);
        }

        private void siftDown(int rootIndex) {
            int currentIndex = rootIndex;

            while (true) {
                int largestIndex = largestIndexInSubtree(currentIndex);

                if (largestIndex == currentIndex) {
                    return;
                }

                swap(currentIndex, largestIndex);
                currentIndex = largestIndex;
            }
        }

        private int largestIndexInSubtree(int rootIndex) {
            int largestIndex = rootIndex;
            int leftChildIndex = leftChildIndex(rootIndex);
            int rightChildIndex = rightChildIndex(rootIndex);

            if (isGreater(leftChildIndex, largestIndex)) {
                largestIndex = leftChildIndex;
            }

            if (isGreater(rightChildIndex, largestIndex)) {
                largestIndex = rightChildIndex;
            }

            return largestIndex;
        }

        private boolean isGreater(int candidateIndex, int currentIndex) {
            return candidateIndex < heapSize && values[candidateIndex] > values[currentIndex];
        }

        private static int leftChildIndex(int index) {
            return 2 * index + 1;
        }

        private static int rightChildIndex(int index) {
            return 2 * index + 2;
        }

        private void swap(int firstIndex, int secondIndex) {
            int value = values[firstIndex];
            values[firstIndex] = values[secondIndex];
            values[secondIndex] = value;
        }
    }

    public static void main(String[] args) {
        HeapSortDemo.main(args);
    }
}
