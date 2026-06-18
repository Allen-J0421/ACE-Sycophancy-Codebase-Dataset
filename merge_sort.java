import java.util.Arrays;

final class MergeSort {

    private MergeSort() {
        // Utility class.
    }

    static void sort(int[] values) {
        if (values == null) {
            throw new IllegalArgumentException("values must not be null");
        }
        if (values.length < 2) {
            return;
        }

        int[] buffer = new int[values.length];
        sort(values, buffer, 0, values.length - 1);
    }

    private static void sort(int[] values, int[] buffer, int left, int right) {
        if (left >= right) {
            return;
        }

        int mid = left + (right - left) / 2;
        sort(values, buffer, left, mid);
        sort(values, buffer, mid + 1, right);
        merge(values, buffer, left, mid, right);
    }

    private static void merge(int[] values, int[] buffer, int left, int mid, int right) {
        System.arraycopy(values, left, buffer, left, right - left + 1);

        int leftIndex = left;
        int rightIndex = mid + 1;
        int destIndex = left;

        while (leftIndex <= mid && rightIndex <= right) {
            if (buffer[leftIndex] <= buffer[rightIndex]) {
                values[destIndex++] = buffer[leftIndex++];
            } else {
                values[destIndex++] = buffer[rightIndex++];
            }
        }

        while (leftIndex <= mid) {
            values[destIndex++] = buffer[leftIndex++];
        }
    }

    public static void main(String[] args) {
        int[] values = {38, 27, 43, 10};

        sort(values);

        System.out.println(Arrays.toString(values));
    }
}
