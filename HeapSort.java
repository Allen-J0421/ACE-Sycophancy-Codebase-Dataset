import java.util.Arrays;
import java.util.Objects;

public final class HeapSort {

    private HeapSort() {
        // Utility class; do not instantiate.
    }

    public static void heapSort(int[] values) {
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

    public static void main(String[] args) {
        int[] values = {9, 4, 3, 8, 10, 2, 5};
        heapSort(values);
        System.out.println(Arrays.toString(values));
    }
}
