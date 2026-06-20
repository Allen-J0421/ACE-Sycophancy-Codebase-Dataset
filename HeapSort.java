import java.util.Arrays;

public final class HeapSort {

    private HeapSort() {
    }

    public static void sort(int[] values) {
        if (values.length < 2) {
            return;
        }

        new MaxHeap(values).sortValues();
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

        private void sortValues() {
            heapifyAllParents();

            for (int end = heapSize - 1; end > 0; end--) {
                moveMaxToSortedSuffix(end);
            }
        }

        private void heapifyAllParents() {
            for (int parentIndex = lastParentIndex(); parentIndex >= 0; parentIndex--) {
                siftDown(parentIndex);
            }
        }

        private void moveMaxToSortedSuffix(int sortedSuffixStart) {
            swap(0, sortedSuffixStart);
            heapSize--;
            siftDownFromRoot();
        }

        private void siftDownFromRoot() {
            siftDown(0);
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

            if (hasGreaterValueAt(leftChildIndex, largestIndex)) {
                largestIndex = leftChildIndex;
            }

            if (hasGreaterValueAt(rightChildIndex, largestIndex)) {
                largestIndex = rightChildIndex;
            }

            return largestIndex;
        }

        private boolean hasGreaterValueAt(int candidateIndex, int currentLargestIndex) {
            return candidateIndex < heapSize && values[candidateIndex] > values[currentLargestIndex];
        }

        private int lastParentIndex() {
            return heapSize / 2 - 1;
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
