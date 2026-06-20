class BubbleSort {

    private static final String SORTED_ARRAY_LABEL = "Sorted array: ";

    static void bubbleSort(int[] values) {
        bubbleSort(values, values.length);
    }

    static void bubbleSort(int[] values, int length) {
        for (int i = 0; i < length - 1; i++) {
            if (!bubblePass(values, length - i)) {
                break;
            }
        }
    }

    private static boolean bubblePass(int[] values, int unsortedLength) {
        boolean swapped = false;

        for (int j = 0; j < unsortedLength - 1; j++) {
            if (values[j] > values[j + 1]) {
                swap(values, j, j + 1);
                swapped = true;
            }
        }

        return swapped;
    }

    private static void swap(int[] values, int firstIndex, int secondIndex) {
        int temp = values[firstIndex];
        values[firstIndex] = values[secondIndex];
        values[secondIndex] = temp;
    }

    static void printArray(int[] values, int length) {
        System.out.println(formatArray(values, length));
    }

    private static String formatArray(int[] values, int length) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < length; i++) {
            result.append(values[i]).append(" ");
        }

        return result.toString();
    }

    private static int[] createSampleNumbers() {
        return new int[] {64, 34, 25, 12, 22, 11, 90};
    }

    public static void main(String[] args) {
        int[] numbers = createSampleNumbers();

        bubbleSort(numbers);

        System.out.println(SORTED_ARRAY_LABEL);
        printArray(numbers, numbers.length);
    }
}
