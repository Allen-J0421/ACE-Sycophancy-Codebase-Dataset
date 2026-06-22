class BubbleSort {

    private BubbleSort() {
        // Utility class.
    }

    static void bubbleSort(int[] values) {
        if (values == null) {
            throw new IllegalArgumentException("values must not be null");
        }

        for (int end = values.length - 1; end > 0; end--) {
            boolean swapped = false;

            for (int index = 0; index < end; index++) {
                if (values[index] > values[index + 1]) {
                    swap(values, index, index + 1);
                    swapped = true;
                }
            }

            if (!swapped) {
                return;
            }
        }
    }

    private static int[] createDemoValues() {
        return new int[] { 64, 34, 25, 12, 22, 11, 90 };
    }

    private static void swap(int[] values, int leftIndex, int rightIndex) {
        int temp = values[leftIndex];
        values[leftIndex] = values[rightIndex];
        values[rightIndex] = temp;
    }

    private static void printArray(int[] values) {
        for (int value : values) {
            System.out.print(value + " ");
        }
        System.out.println();
    }

    private static void runDemo() {
        int[] values = createDemoValues();
        bubbleSort(values);
        System.out.println("Sorted array: ");
        printArray(values);
    }

    public static void main(String[] args) {
        runDemo();
    }
}
