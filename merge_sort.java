class MergeSort {

    private MergeSort() {
    }

    static void sort(int[] values) {
        if (values == null || values.length < 2) {
            return;
        }

        mergeSort(values, 0, values.length - 1);
    }

    private static void mergeSort(int[] values, int left, int right) {
        if (left >= right) {
            return;
        }

        int middle = left + (right - left) / 2;

        mergeSort(values, left, middle);
        mergeSort(values, middle + 1, right);
        merge(values, left, middle, right);
    }

    private static void merge(int[] values, int left, int middle, int right) {
        int leftSize = middle - left + 1;
        int rightSize = right - middle;

        int[] leftValues = new int[leftSize];
        int[] rightValues = new int[rightSize];

        for (int i = 0; i < leftSize; i++) {
            leftValues[i] = values[left + i];
        }

        for (int i = 0; i < rightSize; i++) {
            rightValues[i] = values[middle + 1 + i];
        }

        int leftIndex = 0;
        int rightIndex = 0;
        int mergedIndex = left;

        while (leftIndex < leftSize && rightIndex < rightSize) {
            if (leftValues[leftIndex] <= rightValues[rightIndex]) {
                values[mergedIndex++] = leftValues[leftIndex++];
            } else {
                values[mergedIndex++] = rightValues[rightIndex++];
            }
        }

        while (leftIndex < leftSize) {
            values[mergedIndex++] = leftValues[leftIndex++];
        }

        while (rightIndex < rightSize) {
            values[mergedIndex++] = rightValues[rightIndex++];
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
