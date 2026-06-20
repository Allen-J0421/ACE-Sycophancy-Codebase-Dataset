class BubbleSort {

    private static final String SORTED_ARRAY_LABEL = "Sorted array: ";

    static void bubbleSort(int[] arr) {
        bubbleSort(arr, arr.length);
    }

    static void bubbleSort(int[] arr, int n) {
        for (int i = 0; i < n - 1; i++) {
            if (!bubblePass(arr, n - i)) {
                break;
            }
        }
    }

    private static boolean bubblePass(int[] arr, int unsortedLength) {
        boolean swapped = false;

        for (int j = 0; j < unsortedLength - 1; j++) {
            if (arr[j] > arr[j + 1]) {
                swap(arr, j, j + 1);
                swapped = true;
            }
        }

        return swapped;
    }

    private static void swap(int[] arr, int firstIndex, int secondIndex) {
        int temp = arr[firstIndex];
        arr[firstIndex] = arr[secondIndex];
        arr[secondIndex] = temp;
    }

    static void printArray(int[] arr, int size) {
        System.out.println(formatArray(arr, size));
    }

    private static String formatArray(int[] arr, int size) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < size; i++) {
            result.append(arr[i]).append(" ");
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
