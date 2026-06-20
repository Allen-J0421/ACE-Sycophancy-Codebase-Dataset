import java.util.Arrays;
import java.util.Objects;

final class HeapSort {
    private static final int ROOT_INDEX = 0;

    private HeapSort() {
    }

    static void sort(int[] values) {
        Objects.requireNonNull(values, "values");

        if (values.length < 2) {
            return;
        }

        buildMaxHeap(values);
        for (int heapSize = values.length - 1; heapSize > 0; heapSize--) {
            swap(values, ROOT_INDEX, heapSize);
            siftDown(values, heapSize, ROOT_INDEX);
        }
    }

    static void heapSort(int[] values) {
        sort(values);
    }

    private static void buildMaxHeap(int[] values) {
        for (int index = values.length / 2 - 1; index >= 0; index--) {
            siftDown(values, values.length, index);
        }
    }

    private static void siftDown(int[] values, int heapSize, int rootIndex) {
        int currentIndex = rootIndex;

        while (true) {
            int largestIndex = largestIndexInSubtree(values, heapSize, currentIndex);

            if (largestIndex == currentIndex) {
                return;
            }

            swap(values, currentIndex, largestIndex);
            currentIndex = largestIndex;
        }
    }

    private static int largestIndexInSubtree(int[] values, int heapSize, int rootIndex) {
        int largestIndex = rootIndex;
        int leftChildIndex = leftChildIndex(rootIndex);
        int rightChildIndex = rightChildIndex(rootIndex);

        if (isGreater(values, leftChildIndex, largestIndex, heapSize)) {
            largestIndex = leftChildIndex;
        }

        if (isGreater(values, rightChildIndex, largestIndex, heapSize)) {
            largestIndex = rightChildIndex;
        }

        return largestIndex;
    }

    private static boolean isGreater(int[] values, int candidateIndex, int currentIndex, int heapSize) {
        return candidateIndex < heapSize && values[candidateIndex] > values[currentIndex];
    }

    private static int leftChildIndex(int index) {
        return 2 * index + 1;
    }

    private static int rightChildIndex(int index) {
        return 2 * index + 2;
    }

    private static void swap(int[] values, int firstIndex, int secondIndex) {
        int value = values[firstIndex];
        values[firstIndex] = values[secondIndex];
        values[secondIndex] = value;
    }

    public static void main(String[] args) {
        int[] values = {9, 4, 3, 8, 10, 2, 5};

        sort(values);

        System.out.println(Arrays.toString(values));
    }
}
