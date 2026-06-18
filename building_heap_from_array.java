class BuildHeap {
    private static void heapify(int[] values, int heapSize, int rootIndex) {
        int largestIndex = rootIndex;
        int leftChildIndex = leftChildIndex(rootIndex);
        int rightChildIndex = rightChildIndex(rootIndex);

        if (leftChildIndex < heapSize && values[leftChildIndex] > values[largestIndex]) {
            largestIndex = leftChildIndex;
        }

        if (rightChildIndex < heapSize && values[rightChildIndex] > values[largestIndex]) {
            largestIndex = rightChildIndex;
        }

        if (largestIndex == rootIndex) {
            return;
        }

        swap(values, rootIndex, largestIndex);
        heapify(values, heapSize, largestIndex);
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
        int firstParentIndex = values.length / 2 - 1;

        for (int index = firstParentIndex; index >= 0; index--) {
            heapify(values, values.length, index);
        }
    }

    private static void printValues(int[] values) {
        for (int value : values) {
            System.out.print(value + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        int[] values = {1, 3, 5, 4, 6, 13, 10, 9, 8, 15, 17};

        buildHeap(values);
        printValues(values);
    }
}
