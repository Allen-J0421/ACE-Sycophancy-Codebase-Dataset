public final class HeapSort {
    private HeapSort() {
    }

    public static void sort(int[] values) {
        if (values.length < 2) {
            return;
        }

        new MaxHeap(values).sortValues();
    }

    private static final class MaxHeap {
        private final HeapStorage storage;
        private final HeapWindow window;

        private MaxHeap(int[] values) {
            this.storage = new HeapStorage(values);
            this.window = new HeapWindow(values.length);
        }

        private void sortValues() {
            heapifyAllParents();

            for (int end = window.lastIndex(); end > 0; end--) {
                moveRootToSortedSuffix(end);
            }
        }

        private void heapifyAllParents() {
            for (int parentIndex = window.lastParentIndex(); parentIndex >= 0; parentIndex--) {
                siftDown(parentIndex);
            }
        }

        private void moveRootToSortedSuffix(int sortedSuffixStart) {
            storage.swap(window.rootIndex(), sortedSuffixStart);
            window.excludeLastValue();
            siftDownFromRoot();
        }

        private void siftDownFromRoot() {
            siftDown(window.rootIndex());
        }

        private void siftDown(int rootIndex) {
            int currentIndex = rootIndex;

            while (window.hasChild(currentIndex)) {
                int largerChildIndex = largerChildIndex(currentIndex);

                if (!hasGreaterValueAt(largerChildIndex, currentIndex)) {
                    return;
                }

                storage.swap(currentIndex, largerChildIndex);
                currentIndex = largerChildIndex;
            }
        }

        private int largerChildIndex(int parentIndex) {
            int leftChildIndex = window.leftChildIndex(parentIndex);
            int rightChildIndex = window.rightChildIndex(parentIndex);

            if (hasGreaterValueAt(rightChildIndex, leftChildIndex)) {
                return rightChildIndex;
            }

            return leftChildIndex;
        }

        private boolean hasGreaterValueAt(int candidateIndex, int currentLargestIndex) {
            return window.contains(candidateIndex)
                    && storage.hasGreaterValueAt(candidateIndex, currentLargestIndex);
        }
    }

    private static final class HeapWindow {
        private static final int ROOT_INDEX = 0;
        private int endExclusive;

        private HeapWindow(int length) {
            this.endExclusive = length;
        }

        private boolean contains(int index) {
            return index < endExclusive;
        }

        private void excludeLastValue() {
            endExclusive--;
        }

        private int lastIndex() {
            return endExclusive - 1;
        }

        private int lastParentIndex() {
            return endExclusive / 2 - 1;
        }

        private int rootIndex() {
            return ROOT_INDEX;
        }

        private boolean hasChild(int parentIndex) {
            return contains(leftChildIndex(parentIndex));
        }

        private int leftChildIndex(int parentIndex) {
            return 2 * parentIndex + 1;
        }

        private int rightChildIndex(int parentIndex) {
            return 2 * parentIndex + 2;
        }
    }

    private static final class HeapStorage {
        private final int[] values;

        private HeapStorage(int[] values) {
            this.values = values;
        }

        private boolean hasGreaterValueAt(int candidateIndex, int currentIndex) {
            return values[candidateIndex] > values[currentIndex];
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
