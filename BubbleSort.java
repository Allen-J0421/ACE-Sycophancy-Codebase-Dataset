public final class BubbleSort {
    private BubbleSort() {
        // Utility class.
    }

    public static void sort(int[] values) {
        for (int i = 0; i < values.length - 1; i++) {
            boolean swapped = false;

            for (int j = 0; j < values.length - i - 1; j++) {
                if (values[j] > values[j + 1]) {
                    swap(values, j, j + 1);
                    swapped = true;
                }
            }

            if (!swapped) {
                return;
            }
        }
    }

    public static void printArray(int[] values) {
        StringBuilder output = new StringBuilder();

        for (int value : values) {
            if (!output.isEmpty()) {
                output.append(' ');
            }
            output.append(value);
        }

        System.out.println(output);
    }

    public static void main(String[] args) {
        int[] values = {64, 34, 25, 12, 22, 11, 90};

        sort(values);

        System.out.println("Sorted array: ");
        printArray(values);
    }

    private static void swap(int[] values, int leftIndex, int rightIndex) {
        int temporary = values[leftIndex];
        values[leftIndex] = values[rightIndex];
        values[rightIndex] = temporary;
    }
}
