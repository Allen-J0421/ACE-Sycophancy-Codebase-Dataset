import java.util.Arrays;

public final class BuildHeap {

    private BuildHeap() {
        // Utility class.
    }

    public static void buildHeap(int[] values) {
        validateNotNull(values);

        if (values.length < 2) {
            return;
        }

        for (int parentIndex = lastParentIndex(values.length); parentIndex >= 0; parentIndex--) {
            siftDown(values, values.length, parentIndex);
        }
    }

    public static int[] buildHeapCopy(int[] values) {
        validateNotNull(values);

        int[] heap = Arrays.copyOf(values, values.length);
        buildHeap(heap);
        return heap;
    }

    public static boolean isMaxHeap(int[] values) {
        validateNotNull(values);

        for (int parentIndex = 0; parentIndex <= lastParentIndex(values.length); parentIndex++) {
            int leftChildIndex = leftChildIndex(parentIndex);
            int rightChildIndex = rightChildIndex(parentIndex);

            if (leftChildIndex < values.length && values[parentIndex] < values[leftChildIndex]) {
                return false;
            }

            if (rightChildIndex < values.length && values[parentIndex] < values[rightChildIndex]) {
                return false;
            }
        }

        return true;
    }

    private static void siftDown(int[] values, int heapSize, int parentIndex) {
        int currentIndex = parentIndex;

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

    private static int lastParentIndex(int heapSize) {
        return (heapSize / 2) - 1;
    }

    private static int leftChildIndex(int parentIndex) {
        return (2 * parentIndex) + 1;
    }

    private static int rightChildIndex(int parentIndex) {
        return (2 * parentIndex) + 2;
    }

    private static void swap(int[] values, int firstIndex, int secondIndex) {
        int temp = values[firstIndex];
        values[firstIndex] = values[secondIndex];
        values[secondIndex] = temp;
    }

    private static void validateNotNull(int[] values) {
        if (values == null) {
            throw new IllegalArgumentException("values must not be null");
        }
    }

}
