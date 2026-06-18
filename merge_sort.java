import java.util.Arrays;

final class MergeSort {

    private MergeSort() {
        // Utility class.
    }

    static void sort(int[] values) {
        if (values == null) {
            throw new IllegalArgumentException("values must not be null");
        }
        sort(values, 0, values.length);
    }

    static void sort(int[] values, int fromInclusive, int toExclusive) {
        if (values == null) {
            throw new IllegalArgumentException("values must not be null");
        }
        validateRange(values.length, fromInclusive, toExclusive);
        if (toExclusive - fromInclusive < 2) {
            return;
        }

        int[] buffer = new int[values.length];
        sort(values, buffer, fromInclusive, toExclusive);
    }

    private static void validateRange(int length, int fromInclusive, int toExclusive) {
        if (fromInclusive < 0 || toExclusive > length || fromInclusive > toExclusive) {
            throw new IndexOutOfBoundsException(
                "Invalid range: [" + fromInclusive + ", " + toExclusive + ") for length " + length
            );
        }
    }

    private static void sort(int[] values, int[] buffer, int left, int right) {
        if (right - left < 2) {
            return;
        }

        int mid = left + (right - left) / 2;
        sort(values, buffer, left, mid);
        sort(values, buffer, mid, right);
        merge(values, buffer, left, mid, right);
    }

    private static void merge(int[] values, int[] buffer, int left, int mid, int right) {
        System.arraycopy(values, left, buffer, left, right - left);

        int leftIndex = left;
        int rightIndex = mid;
        int destIndex = left;

        while (leftIndex < mid && rightIndex < right) {
            if (buffer[leftIndex] <= buffer[rightIndex]) {
                values[destIndex++] = buffer[leftIndex++];
            } else {
                values[destIndex++] = buffer[rightIndex++];
            }
        }

        while (leftIndex < mid) {
            values[destIndex++] = buffer[leftIndex++];
        }
    }

    public static void main(String[] args) {
        int[] values = {38, 27, 43, 10};

        sort(values);

        System.out.println(Arrays.toString(values));
    }
}
