import java.util.Arrays;

public final class BuildHeap {

    private BuildHeap() {
        // Utility class.
    }

    public static void buildHeap(int[] values) {
        new HeapView(values).buildMaxHeap();
    }

    public static int[] buildHeapCopy(int[] values) {
        int[] heap = Arrays.copyOf(validateNotNull(values), values.length);
        buildHeap(heap);
        return heap;
    }

    public static boolean isMaxHeap(int[] values) {
        return new HeapView(values).isMaxHeap();
    }

    private static int[] validateNotNull(int[] values) {
        if (values == null) {
            throw new IllegalArgumentException("values must not be null");
        }

        return values;
    }

    private static final class HeapView {

        private final int[] values;
        private final int size;

        private HeapView(int[] values) {
            this.values = validateNotNull(values);
            this.size = values.length;
        }

        private void buildMaxHeap() {
            if (size < 2) {
                return;
            }

            for (int parentIndex = lastParentIndex(); parentIndex >= 0; parentIndex--) {
                siftDown(parentIndex);
            }
        }

        private boolean isMaxHeap() {
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
}
