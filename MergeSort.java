/**
 * Sorts integer arrays in ascending order using merge sort.
 */
public final class MergeSort {

    private MergeSort() {
    }

    /**
     * Sorts the provided array in place. Null and single-element arrays are left unchanged.
     */
    public static void sort(int[] values) {
        if (values == null || values.length < 2) {
            return;
        }

        int[] buffer = new int[values.length];
        mergeSort(values, buffer, 0, values.length);
    }

    private static void mergeSort(int[] values, int[] buffer, int start, int endExclusive) {
        if (endExclusive - start < 2) {
            return;
        }

        int split = start + (endExclusive - start) / 2;

        mergeSort(values, buffer, start, split);
        mergeSort(values, buffer, split, endExclusive);

        if (isOrderedAcrossBoundary(values, split)) {
            return;
        }

        merge(values, buffer, start, split, endExclusive);
    }

    private static boolean isOrderedAcrossBoundary(int[] values, int split) {
        return values[split - 1] <= values[split];
    }

    private static void merge(int[] values, int[] buffer, int start, int split, int endExclusive) {
        System.arraycopy(values, start, buffer, start, endExclusive - start);

        int leftIndex = start;
        int rightIndex = split;
        int mergedIndex = start;

        while (leftIndex < split && rightIndex < endExclusive) {
            if (buffer[leftIndex] <= buffer[rightIndex]) {
                values[mergedIndex++] = buffer[leftIndex++];
            } else {
                values[mergedIndex++] = buffer[rightIndex++];
            }
        }

        while (leftIndex < split) {
            values[mergedIndex++] = buffer[leftIndex++];
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
