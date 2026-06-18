public final class MergeSort {

    private MergeSort() {
    }

    public static void sort(int[] values) {
        if (values == null || values.length < 2) {
            return;
        }

        int[] buffer = new int[values.length];
        mergeSort(values, buffer, 0, values.length);
    }

    private static void mergeSort(int[] values, int[] buffer, int start, int end) {
        if (end - start < 2) {
            return;
        }

        int middle = start + (end - start) / 2;

        mergeSort(values, buffer, start, middle);
        mergeSort(values, buffer, middle, end);
        merge(values, buffer, start, middle, end);
    }

    private static void merge(int[] values, int[] buffer, int start, int middle, int end) {
        System.arraycopy(values, start, buffer, start, end - start);

        int leftIndex = start;
        int rightIndex = middle;
        int mergedIndex = start;

        while (leftIndex < middle && rightIndex < end) {
            if (buffer[leftIndex] <= buffer[rightIndex]) {
                values[mergedIndex++] = buffer[leftIndex++];
            } else {
                values[mergedIndex++] = buffer[rightIndex++];
            }
        }

        while (leftIndex < middle) {
            values[mergedIndex++] = buffer[leftIndex++];
        }

        while (rightIndex < end) {
            values[mergedIndex++] = buffer[rightIndex++];
        }
    }

    public static void main(String[] args) {
        int[] values = {38, 27, 43, 10};

        sort(values);
        printValues(values);
    }

    private static void printValues(int[] values) {
        System.out.println(formatValues(values));
    }

    private static String formatValues(int[] values) {
        if (values.length == 0) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        result.append(values[0]);

        for (int i = 1; i < values.length; i++) {
            result.append(' ').append(values[i]);
        }

        return result.toString();
    }
}
