import java.util.Objects;

public final class BuildHeap {

    private BuildHeap() {
        // Utility class.
    }

    public static void siftDown(int[] values, int heapSize, int rootIndex) {
        validateHeapArguments(values, heapSize, rootIndex);

        int currentIndex = rootIndex;

        while (true) {
            int largestIndex = currentIndex;
            int leftChildIndex = 2 * currentIndex + 1;
            int rightChildIndex = 2 * currentIndex + 2;

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
        Objects.requireNonNull(values, "values");

        buildMaxHeap(values, values.length);
    }

    public static boolean isMaxHeap(int[] values) {
        Objects.requireNonNull(values, "values");

        for (int parentIndex = 0; parentIndex < values.length / 2; parentIndex++) {
            int leftChildIndex = 2 * parentIndex + 1;
            int rightChildIndex = 2 * parentIndex + 2;

            if (leftChildIndex < values.length && values[parentIndex] < values[leftChildIndex]) {
                return false;
            }

            if (rightChildIndex < values.length && values[parentIndex] < values[rightChildIndex]) {
                return false;
            }
        }

        return true;
    }

    public static void buildMaxHeap(int[] values, int heapSize) {
        Objects.requireNonNull(values, "values");

        if (heapSize == 0) {
            return;
        }

        validateHeapArguments(values, heapSize, 0);

        if (heapSize < 2) {
            return;
        }

        for (int i = (heapSize / 2) - 1; i >= 0; i--) {
            siftDown(values, heapSize, i);
        }
    }

    private static void swap(int[] values, int i, int j) {
        int temp = values[i];
        values[i] = values[j];
        values[j] = temp;
    }

    private static void validateHeapArguments(int[] values, int heapSize, int rootIndex) {
        Objects.requireNonNull(values, "values");

        if (heapSize < 0 || heapSize > values.length) {
            throw new IllegalArgumentException("heapSize must be between 0 and values.length");
        }

        if (rootIndex < 0 || rootIndex >= heapSize) {
            throw new IllegalArgumentException("rootIndex must be within the heap range");
        }
    }
}
