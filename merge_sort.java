class MergeSort {

    private MergeSort() {
        // Utility class.
    }

    static void sort(int[] values) {
        if (values == null || values.length < 2) {
            return;
        }

        int[] scratch = new int[values.length];
        mergeSort(values, scratch, 0, values.length - 1);
    }

    private static void mergeSort(int[] values, int[] scratch, int left, int right) {
        if (left >= right) {
            return;
        }

        int middle = left + (right - left) / 2;
        mergeSort(values, scratch, left, middle);
        mergeSort(values, scratch, middle + 1, right);
        merge(values, scratch, left, middle, right);
    }

    private static void merge(int[] values, int[] scratch, int left, int middle, int right) {
        int leftIndex = left;
        int rightIndex = middle + 1;
        int scratchIndex = left;

        while (leftIndex <= middle && rightIndex <= right) {
            if (values[leftIndex] <= values[rightIndex]) {
                scratch[scratchIndex++] = values[leftIndex++];
            } else {
                scratch[scratchIndex++] = values[rightIndex++];
            }
        }

        while (leftIndex <= middle) {
            scratch[scratchIndex++] = values[leftIndex++];
        }

        while (rightIndex <= right) {
            scratch[scratchIndex++] = values[rightIndex++];
        }

        for (int index = left; index <= right; index++) {
            values[index] = scratch[index];
        }
    }

    private static void printArray(int[] values) {
        for (int value : values) {
            System.out.print(value + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        int[] values = {38, 27, 43, 10};

        sort(values);
        printArray(values);
    }
}
