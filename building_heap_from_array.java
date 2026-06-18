final class BuildHeap {
    private BuildHeap() {
    }

    private static void siftDown(int[] values, int heapSize, int startIndex) {
        int currentIndex = startIndex;

        while (hasLeftChild(currentIndex, heapSize)) {
            int largerChildIndex = largerChildIndex(values, heapSize, currentIndex);

            if (values[currentIndex] >= values[largerChildIndex]) {
                return;
            }

            swap(values, currentIndex, largerChildIndex);
            currentIndex = largerChildIndex;
        }
    }

    private static boolean hasLeftChild(int index, int heapSize) {
        return leftChildIndex(index) < heapSize;
    }

    private static int largerChildIndex(int[] values, int heapSize, int parentIndex) {
        int leftChildIndex = leftChildIndex(parentIndex);
        int rightChildIndex = rightChildIndex(parentIndex);

        if (rightChildIndex < heapSize && values[rightChildIndex] > values[leftChildIndex]) {
            return rightChildIndex;
        }

        return leftChildIndex;
    }

    private static int leftChildIndex(int index) {
        return 2 * index + 1;
    }

    private static int rightChildIndex(int index) {
        return 2 * index + 2;
    }

    private static void swap(int[] values, int firstIndex, int secondIndex) {
        int temp = values[firstIndex];
        values[firstIndex] = values[secondIndex];
        values[secondIndex] = temp;
    }

    static void buildHeap(int[] values) {
        for (int index = lastParentIndex(values.length); index >= 0; index--) {
            siftDown(values, values.length, index);
        }
    }

    private static int lastParentIndex(int heapSize) {
        return heapSize / 2 - 1;
    }

    private static String formatValues(int[] values) {
        StringBuilder output = new StringBuilder();

        for (int value : values) {
            output.append(value).append(' ');
        }

        return output.toString();
    }

    private static int[] sampleValues() {
        return new int[] {1, 3, 5, 4, 6, 13, 10, 9, 8, 15, 17};
    }

    public static void main(String[] args) {
        int[] values = sampleValues();

        buildHeap(values);
        System.out.println(formatValues(values));
    }
}
