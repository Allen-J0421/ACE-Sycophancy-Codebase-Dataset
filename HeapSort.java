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

    private static final class MaxHeap {
        private final HeapStorage storage;
        private final UnsortedPrefix unsortedPrefix;

        private MaxHeap(int[] values) {
            this.storage = new HeapStorage(values);
            this.unsortedPrefix = new UnsortedPrefix(values.length);
        }

        private void sortValues() {
            heapifyAllParents();

            for (int end = unsortedPrefix.lastIndex(); end > 0; end--) {
                moveRootToSortedSuffix(end);
            }
        }

        private void heapifyAllParents() {
            for (int parentIndex = unsortedPrefix.lastParentIndex(); parentIndex >= 0; parentIndex--) {
                siftDown(parentIndex);
            }
        }

        private void moveRootToSortedSuffix(int sortedSuffixStart) {
            storage.swap(ROOT_INDEX, sortedSuffixStart);
            unsortedPrefix.excludeLastValue();
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

                storage.swap(currentIndex, largerChildIndex);
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
            return unsortedPrefix.contains(candidateIndex)
                    && storage.hasGreaterValueAt(candidateIndex, currentLargestIndex);
        }

        private int leftChildIndex(int parentIndex) {
            return 2 * parentIndex + 1;
        }

        private int rightChildIndex(int parentIndex) {
            return 2 * parentIndex + 2;
        }

        private boolean hasChild(int parentIndex) {
            return unsortedPrefix.contains(leftChildIndex(parentIndex));
        }
    }

    private static final class UnsortedPrefix {
        private int endExclusive;

        private UnsortedPrefix(int length) {
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
