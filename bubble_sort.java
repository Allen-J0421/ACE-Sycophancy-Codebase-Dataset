class BubbleSort {

    static void bubbleSort(int[] arr) {
        bubbleSort(arr, arr.length);
    }

    static void bubbleSort(int[] arr, int n) {
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;

            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    swap(arr, j, j + 1);
                    swapped = true;
                }
            }

            if (!swapped) {
                break;
            }
        }
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

    public static void main(String[] args) {
        int[] numbers = {64, 34, 25, 12, 22, 11, 90};

        bubbleSort(numbers);

        System.out.println("Sorted array: ");
        printArray(numbers, numbers.length);
    }
}
