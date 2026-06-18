class MergeSort {

    private MergeSort() {
    }

    static void sort(int[] values) {
        if (values == null || values.length < 2) {
            return;
        }

        int[] buffer = new int[values.length];
        mergeSort(values, buffer, 0, values.length - 1);
    }

    private static void mergeSort(int[] values, int[] buffer, int left, int right) {
        if (left >= right) {
            return;
        }

        int middle = left + (right - left) / 2;

        mergeSort(values, buffer, left, middle);
        mergeSort(values, buffer, middle + 1, right);
        merge(values, buffer, left, middle, right);
    }

    private static void merge(int[] values, int[] buffer, int left, int middle, int right) {
        System.arraycopy(values, left, buffer, left, right - left + 1);

        int leftIndex = left;
        int rightIndex = middle + 1;
        int mergedIndex = left;

        while (leftIndex <= middle && rightIndex <= right) {
            if (buffer[leftIndex] <= buffer[rightIndex]) {
                values[mergedIndex++] = buffer[leftIndex++];
            } else {
                values[mergedIndex++] = buffer[rightIndex++];
            }
        }

        while (leftIndex <= middle) {
            values[mergedIndex++] = buffer[leftIndex++];
        }

        while (rightIndex <= right) {
            values[mergedIndex++] = buffer[rightIndex++];
        }
    }

    public static void main(String[] args) {
        int[] values = {38, 27, 43, 10};

        sort(values);
        printValues(values);
    }

    private static void printValues(int[] values) {
        for (int value : values) {
            System.out.print(value + " ");
        }
        System.out.println();
    }
}
