import java.util.Arrays;

class BuildHeap {

    private BuildHeap() {
        // Utility class.
    }

    static void buildHeap(int[] values) {
        for (int parentIndex = lastParentIndex(values.length); parentIndex >= 0; parentIndex--) {
            siftDown(values, values.length, parentIndex);
        }
    }

    private static void siftDown(int[] values, int heapSize, int parentIndex) {
        int currentIndex = parentIndex;

        while (true) {
            int largestIndex = currentIndex;
            int leftChildIndex = leftChildIndex(currentIndex);
            int rightChildIndex = rightChildIndex(currentIndex);

            if (leftChildIndex < heapSize && values[leftChildIndex] > values[largestIndex]) {
                largestIndex = leftChildIndex;
            }

            if (rightChildIndex < heapSize && values[rightChildIndex] > values[largestIndex]) {
                largestIndex = rightChildIndex;
            }

            if (largestIndex == currentIndex) {
                return;
            }

            swap(values, currentIndex, largestIndex);
            currentIndex = largestIndex;
        }
    }

    private static int lastParentIndex(int heapSize) {
        return (heapSize / 2) - 1;
    }

    private static int leftChildIndex(int parentIndex) {
        return (2 * parentIndex) + 1;
    }

    private static int rightChildIndex(int parentIndex) {
        return (2 * parentIndex) + 2;
    }

    private static void swap(int[] values, int firstIndex, int secondIndex) {
        int temp = values[firstIndex];
        values[firstIndex] = values[secondIndex];
        values[secondIndex] = temp;
    }

    public static void main(String[] args) {
        int[] values = {1, 3, 5, 4, 6, 13, 10, 9, 8, 15, 17};
        buildHeap(values);
        System.out.println(Arrays.toString(values));
    }
}
