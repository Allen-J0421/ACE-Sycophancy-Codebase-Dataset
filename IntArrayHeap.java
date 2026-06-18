final class IntArrayHeap {

    private final int[] values;
    private final int size;

    private IntArrayHeap(int[] values) {
        this.values = IntArrays.requireValues(values);
        this.size = values.length;
    }

    static void buildInPlace(int[] values) {
        new IntArrayHeap(values).buildMaxHeap();
    }

    static int[] buildCopy(int[] values) {
        return new IntArrayHeap(IntArrays.copyOf(values)).buildMaxHeap().toArray();
    }

    static boolean isMaxHeap(int[] values) {
        return new IntArrayHeap(values).hasMaxHeapOrder();
    }

    static HeapBuildResult analyze(int[] values) {
        return new IntArrayHeap(values).analyzeHeap();
    }

    IntArrayHeap buildMaxHeap() {
        if (size < 2) {
            return this;
        }

        for (int parentIndex = lastParentIndex(); parentIndex >= 0; parentIndex--) {
            siftDown(parentIndex);
        }

        return this;
    }

    private boolean hasMaxHeapOrder() {
        for (int parentIndex = 0; parentIndex <= lastParentIndex(); parentIndex++) {
            if (hasGreaterChild(parentIndex, leftChildIndex(parentIndex))) {
                return false;
            }

            if (hasGreaterChild(parentIndex, rightChildIndex(parentIndex))) {
                return false;
            }
        }

        return true;
    }

    private HeapBuildResult analyzeHeap() {
        IntArrayHeap inputSnapshot = copy();
        IntArrayHeap heapSnapshot = copy().buildMaxHeap();

        return HeapBuildResult.of(
                inputSnapshot.toArray(),
                heapSnapshot.toArray(),
                heapSnapshot.hasMaxHeapOrder());
    }

    int[] toArray() {
        return values;
    }

    private IntArrayHeap copy() {
        return new IntArrayHeap(IntArrays.copyOf(values));
    }

    private void siftDown(int parentIndex) {
        int currentIndex = parentIndex;

        while (true) {
            int largestIndex = currentIndex;
            largestIndex = largerIndex(largestIndex, leftChildIndex(currentIndex));
            largestIndex = largerIndex(largestIndex, rightChildIndex(currentIndex));

            if (largestIndex == currentIndex) {
                return;
            }

            swap(currentIndex, largestIndex);
            currentIndex = largestIndex;
        }
    }

    private boolean hasGreaterChild(int parentIndex, int childIndex) {
        return childIndex < size && values[parentIndex] < values[childIndex];
    }

    private int largerIndex(int currentLargestIndex, int candidateIndex) {
        if (candidateIndex < size && values[candidateIndex] > values[currentLargestIndex]) {
            return candidateIndex;
        }

        return currentLargestIndex;
    }

    private int lastParentIndex() {
        return (size / 2) - 1;
    }

    private int leftChildIndex(int parentIndex) {
        return (2 * parentIndex) + 1;
    }

    private int rightChildIndex(int parentIndex) {
        return (2 * parentIndex) + 2;
    }

    private void swap(int firstIndex, int secondIndex) {
        int temp = values[firstIndex];
        values[firstIndex] = values[secondIndex];
        values[secondIndex] = temp;
    }
}
