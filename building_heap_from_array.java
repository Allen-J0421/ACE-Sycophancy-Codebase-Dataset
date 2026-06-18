class BuildHeap {
    private static void heapify(int[] values, int heapSize, int rootIndex) {
        int currentIndex = rootIndex;

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
            heapify(values, values.length, index);
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

    public static void main(String[] args) {
        int[] values = {1, 3, 5, 4, 6, 13, 10, 9, 8, 15, 17};

        buildHeap(values);
        System.out.println(formatValues(values));
    }
}
