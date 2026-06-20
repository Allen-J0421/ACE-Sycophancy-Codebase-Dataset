import java.util.Arrays;
import java.util.Objects;

final class HeapSort {
    private HeapSort() {
    }

    static void sort(int[] values) {
        Objects.requireNonNull(values, "values");

        buildMaxHeap(values);
        for (int heapSize = values.length - 1; heapSize > 0; heapSize--) {
            swap(values, 0, heapSize);
            siftDown(values, heapSize, 0);
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
        int largestIndex = rootIndex;
        int leftChildIndex = leftChildIndex(rootIndex);
        int rightChildIndex = rightChildIndex(rootIndex);

        if (leftChildIndex < heapSize && values[leftChildIndex] > values[largestIndex]) {
            largestIndex = leftChildIndex;
        }

        if (rightChildIndex < heapSize && values[rightChildIndex] > values[largestIndex]) {
            largestIndex = rightChildIndex;
        }

        if (largestIndex == rootIndex) {
            return;
        }

        swap(values, rootIndex, largestIndex);
        siftDown(values, heapSize, largestIndex);
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
