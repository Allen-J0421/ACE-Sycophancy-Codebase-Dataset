import java.util.Arrays;

public final class HeapSort {

    private HeapSort() {
    }

    public static void sort(int[] values) {
        new MaxHeap(values).sortIntoAscendingOrder();
    }

    public static void main(String[] args) {
        int[] arr = { 9, 4, 3, 8, 10, 2, 5 };

        sort(arr);

        System.out.println(Arrays.toString(arr));
    }

    private static final class MaxHeap {
        private final int[] values;
        private int heapSize;

        private MaxHeap(int[] values) {
            this.values = values;
            this.heapSize = values.length;
        }

        private void sortIntoAscendingOrder() {
            build();

            for (int end = heapSize - 1; end > 0; end--) {
                swap(0, end);
                heapSize--;
                siftDown(0);
            }
        }

        private void build() {
            for (int parentIndex = heapSize / 2 - 1; parentIndex >= 0; parentIndex--) {
                siftDown(parentIndex);
            }
        }

        private void siftDown(int rootIndex) {
            int currentIndex = rootIndex;

            while (true) {
                int largestIndex = indexOfLargestValue(currentIndex);

                if (largestIndex == currentIndex) {
                    return;
                }

                swap(currentIndex, largestIndex);
                currentIndex = largestIndex;
            }
        }

        private int indexOfLargestValue(int parentIndex) {
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

        private int leftChildIndex(int parentIndex) {
            return 2 * parentIndex + 1;
        }

        private int rightChildIndex(int parentIndex) {
            return 2 * parentIndex + 2;
        }

        private void swap(int firstIndex, int secondIndex) {
            if (firstIndex == secondIndex) {
                return;
            }

            int temp = values[firstIndex];
            values[firstIndex] = values[secondIndex];
            values[secondIndex] = temp;
        }
    }
}
