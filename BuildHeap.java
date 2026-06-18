import java.util.Objects;

public final class BuildHeap {

    private BuildHeap() {
        // Utility class.
    }

    static void siftDown(int[] values, int heapSize, int rootIndex) {
        validateHeapRange(values, heapSize);
        validateRootIndex(heapSize, rootIndex);

        int currentIndex = rootIndex;

        while (true) {
            int largestIndex = currentIndex;
            int leftChildIndex = leftChildIndex(currentIndex);
            int rightChildIndex = rightChildIndex(currentIndex);

            if (leftChildIndex < heapSize && values[leftChildIndex] > values[largestIndex]) {
                largestIndex = leftChildIndex;
            }

            if (rightChildIndex < heapSize && values[rightChildIndex] > values[largestIndex]) {
                largestIndex = rightChildIndex;
            }

            if (largestIndex == currentIndex) {
                return;
            }

            swap(values, currentIndex, largestIndex);
            currentIndex = largestIndex;
        }
    }

    public static void buildMaxHeap(int[] values) {
        buildMaxHeap(values, arrayLength(values));
    }

    static void buildMaxHeap(int[] values, int heapSize) {
        validateHeapRange(values, heapSize);

        for (int parentIndex = lastParentIndex(heapSize); parentIndex >= 0; parentIndex--) {
            siftDown(values, heapSize, parentIndex);
        }
    }

    public static boolean isMaxHeap(int[] values) {
        return isMaxHeap(values, arrayLength(values));
    }

    static boolean isMaxHeap(int[] values, int heapSize) {
        validateHeapRange(values, heapSize);

        for (int parentIndex = 0, lastParentIndex = lastParentIndex(heapSize);
                parentIndex <= lastParentIndex;
                parentIndex++) {
            int leftChildIndex = leftChildIndex(parentIndex);
            int rightChildIndex = rightChildIndex(parentIndex);

            if (leftChildIndex < heapSize && values[parentIndex] < values[leftChildIndex]) {
                return false;
            }

            if (rightChildIndex < heapSize && values[parentIndex] < values[rightChildIndex]) {
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
