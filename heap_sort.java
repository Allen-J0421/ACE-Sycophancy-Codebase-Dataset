import java.util.Arrays;
import java.util.Objects;

final class HeapSort {
    private HeapSort() {
    }

    static void sort(int[] values) {
        Objects.requireNonNull(values, "values");

        if (values.length < 2) {
            return;
        }

        MaxHeap.from(values).sortAscending();
    }

    static void heapSort(int[] values) {
        sort(values);
    }

    private static final class MaxHeap {
        private static final int ROOT_INDEX = 0;

        private final int[] values;
        private int size;

        private MaxHeap(int[] values) {
            this.values = values;
            size = values.length;
        }

        private static MaxHeap from(int[] values) {
            MaxHeap heap = new MaxHeap(values);
            heap.build();
            return heap;
        }

        private void sortAscending() {
            while (size > 1) {
                moveMaxToEnd();
                siftDown(ROOT_INDEX);
            }
        }

        private void build() {
            for (int index = firstParentIndex(); index >= 0; index--) {
                siftDown(index);
            }
        }

        private int firstParentIndex() {
            return size / 2 - 1;
        }

        private void moveMaxToEnd() {
            size--;
            swap(ROOT_INDEX, size);
        }

        private void siftDown(int rootIndex) {
            int currentIndex = rootIndex;

            while (true) {
                int largestIndex = largestIndexInSubtree(currentIndex);

                if (largestIndex == currentIndex) {
                    return;
                }

                swap(currentIndex, largestIndex);
                currentIndex = largestIndex;
            }
        }

        private int largestIndexInSubtree(int rootIndex) {
            int largestIndex = rootIndex;
            int leftChildIndex = leftChildIndex(rootIndex);
            int rightChildIndex = rightChildIndex(rootIndex);

            if (isGreater(leftChildIndex, largestIndex)) {
                largestIndex = leftChildIndex;
            }

            if (isGreater(rightChildIndex, largestIndex)) {
                largestIndex = rightChildIndex;
            }

            return largestIndex;
        }

        private boolean isGreater(int candidateIndex, int currentIndex) {
            return candidateIndex < size && values[candidateIndex] > values[currentIndex];
        }

        private static int leftChildIndex(int index) {
            return 2 * index + 1;
        }

        private static int rightChildIndex(int index) {
            return 2 * index + 2;
        }

        private void swap(int firstIndex, int secondIndex) {
            int value = values[firstIndex];
            values[firstIndex] = values[secondIndex];
            values[secondIndex] = value;
        }
    }

    public static void main(String[] args) {
        int[] values = {9, 4, 3, 8, 10, 2, 5};

        sort(values);

        System.out.println(Arrays.toString(values));
    }
}
