class MergeSort {

    private MergeSort() {
        // Utility class.
    }

    static void sort(int[] values) {
        if (values == null || values.length < 2) {
            return;
        }

        int[] scratch = new int[values.length];
        mergeSort(values, scratch, 0, values.length);
    }

    private static void mergeSort(int[] values, int[] scratch, int start, int end) {
        if (end - start < 2) {
            return;
        }

        int middle = start + (end - start) / 2;
        mergeSort(values, scratch, start, middle);
        mergeSort(values, scratch, middle, end);
        merge(values, scratch, start, middle, end);
    }

    private static void merge(int[] values, int[] scratch, int start, int middle, int end) {
        int leftIndex = start;
        int rightIndex = middle;
        int scratchIndex = start;

        while (leftIndex < middle && rightIndex < end) {
            if (values[leftIndex] <= values[rightIndex]) {
                scratch[scratchIndex++] = values[leftIndex++];
            } else {
                scratch[scratchIndex++] = values[rightIndex++];
            }
        }

        while (leftIndex < middle) {
            scratch[scratchIndex++] = values[leftIndex++];
        }

        while (rightIndex < end) {
            scratch[scratchIndex++] = values[rightIndex++];
        }

        System.arraycopy(scratch, start, values, start, end - start);
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
