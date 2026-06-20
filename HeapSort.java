import java.util.Arrays;

public final class HeapSort {
    private static final int ROOT_INDEX = 0;

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
                moveRootToSortedSuffix(end);
            }
        }

        private void heapifyAllParents() {
            for (int parentIndex = lastParentIndex(); parentIndex >= 0; parentIndex--) {
                siftDown(parentIndex);
            }
        }

        private void moveRootToSortedSuffix(int sortedSuffixStart) {
            swap(ROOT_INDEX, sortedSuffixStart);
            shrinkHeap();
            siftDownFromRoot();
        }

        private void siftDownFromRoot() {
            siftDown(ROOT_INDEX);
        }

        private void siftDown(int rootIndex) {
            int currentIndex = rootIndex;

            while (hasChild(currentIndex)) {
                int largerChildIndex = largerChildIndex(currentIndex);

                if (!hasGreaterValueAt(largerChildIndex, currentIndex)) {
                    return;
                }

                swap(currentIndex, largerChildIndex);
                currentIndex = largerChildIndex;
            }
        }

        private int largerChildIndex(int parentIndex) {
            int leftChildIndex = leftChildIndex(parentIndex);
            int rightChildIndex = rightChildIndex(parentIndex);

            if (hasGreaterValueAt(rightChildIndex, leftChildIndex)) {
                return rightChildIndex;
            }

            return leftChildIndex;
        }

        private boolean hasGreaterValueAt(int candidateIndex, int currentLargestIndex) {
            return candidateIndex < heapSize && values[candidateIndex] > values[currentLargestIndex];
        }

        private boolean hasChild(int parentIndex) {
            return leftChildIndex(parentIndex) < heapSize;
        }

        private void shrinkHeap() {
            heapSize--;
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
