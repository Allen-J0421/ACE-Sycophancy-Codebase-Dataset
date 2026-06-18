import java.util.Objects;

/**
 * Utility methods for building and checking max heaps stored in arrays.
 */
public final class BuildHeap {

    private BuildHeap() {
        // Utility class.
    }

    static void siftDown(int[] values, int heapSize, int rootIndex) {
        validateHeapRange(values, heapSize);
        validateRootIndex(heapSize, rootIndex);

        int currentIndex = rootIndex;

        while (true) {
            int largestIndex = indexOfLargestOfParentAndChildren(values, heapSize, currentIndex);

            if (largestIndex == currentIndex) {
                return;
            }

            swap(values, currentIndex, largestIndex);
            currentIndex = largestIndex;
        }
    }

    /**
     * Builds a max heap in place from the full array.
     *
     * @param values the array to transform into a max heap
     * @throws NullPointerException if {@code values} is null
     */
    public static void buildMaxHeap(int[] values) {
        buildMaxHeap(values, arrayLength(values));
    }

    static void buildMaxHeap(int[] values, int heapSize) {
        validateHeapRange(values, heapSize);

        for (int parentIndex = lastParentIndex(heapSize); parentIndex >= 0; parentIndex--) {
            siftDown(values, heapSize, parentIndex);
        }
    }

    /**
     * Returns whether the full array satisfies the max-heap property.
     *
     * @param values the array to inspect
     * @return true if the array is a max heap, otherwise false
     * @throws NullPointerException if {@code values} is null
     */
    public static boolean isMaxHeap(int[] values) {
        return isMaxHeap(values, arrayLength(values));
    }

    static boolean isMaxHeap(int[] values, int heapSize) {
        validateHeapRange(values, heapSize);

        for (int parentIndex = 0, lastParentIndex = lastParentIndex(heapSize);
                parentIndex <= lastParentIndex;
                parentIndex++) {
            if (indexOfLargestOfParentAndChildren(values, heapSize, parentIndex) != parentIndex) {
                return false;
            }
        }

        return true;
    }

    private static void swap(int[] values, int i, int j) {
        int temp = values[i];
        values[i] = values[j];
        values[j] = temp;
    }

    private static int arrayLength(int[] values) {
        Objects.requireNonNull(values, "values");
        return values.length;
    }

    private static int leftChildIndex(int parentIndex) {
        return 2 * parentIndex + 1;
    }

    private static int rightChildIndex(int parentIndex) {
        return 2 * parentIndex + 2;
    }

    private static int indexOfLargestOfParentAndChildren(int[] values, int heapSize, int parentIndex) {
        int largestIndex = parentIndex;
        int leftChildIndex = leftChildIndex(parentIndex);
        int rightChildIndex = rightChildIndex(parentIndex);

        if (leftChildIndex < heapSize && values[leftChildIndex] > values[largestIndex]) {
            largestIndex = leftChildIndex;
        }

        if (rightChildIndex < heapSize && values[rightChildIndex] > values[largestIndex]) {
            largestIndex = rightChildIndex;
        }

        return largestIndex;
    }

    private static int lastParentIndex(int heapSize) {
        return (heapSize / 2) - 1;
    }

    private static void validateHeapRange(int[] values, int heapSize) {
        Objects.requireNonNull(values, "values");

        if (heapSize < 0 || heapSize > values.length) {
            throw new IllegalArgumentException("heapSize must be between 0 and values.length");
        }
    }

    private static void validateRootIndex(int heapSize, int rootIndex) {
        if (rootIndex < 0 || rootIndex >= heapSize) {
            throw new IllegalArgumentException("rootIndex must be within the heap range");
        }
    }
}
