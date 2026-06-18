import java.util.Arrays;
import java.util.Comparator;

public final class MergeSort {

    private MergeSort() {
        // Utility class.
    }

    public static void sort(int[] values) {
        requireValues(values);
        sort(values, 0, values.length);
    }

    public static void sort(int[] values, int fromInclusive, int toExclusive) {
        requireValues(values);
        validateRange(values.length, fromInclusive, toExclusive);
        if (toExclusive - fromInclusive < 2) {
            return;
        }

        int[] buffer = new int[values.length];
        sort(values, buffer, fromInclusive, toExclusive);
    }

    public static <T extends Comparable<? super T>> void sort(T[] values) {
        sort(values, Comparator.naturalOrder());
    }

    public static <T> void sort(T[] values, Comparator<? super T> comparator) {
        requireValues(values);
        requireComparator(comparator);
        if (values.length < 2) {
            return;
        }

        Object[] buffer = new Object[values.length];
        sort(values, buffer, comparator, 0, values.length);
    }

    private static void requireValues(Object values) {
        if (values == null) {
            throw new IllegalArgumentException("values must not be null");
        }
    }

    private static void requireComparator(Object comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException("comparator must not be null");
        }
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

    private static <T> void sort(
        T[] values,
        Object[] buffer,
        Comparator<? super T> comparator,
        int left,
        int right
    ) {
        if (right - left < 2) {
            return;
        }

        int mid = left + (right - left) / 2;
        sort(values, buffer, comparator, left, mid);
        sort(values, buffer, comparator, mid, right);
        merge(values, buffer, comparator, left, mid, right);
    }

    private static <T> void merge(
        T[] values,
        Object[] buffer,
        Comparator<? super T> comparator,
        int left,
        int mid,
        int right
    ) {
        System.arraycopy(values, left, buffer, left, right - left);

        int leftIndex = left;
        int rightIndex = mid;
        int destIndex = left;

        while (leftIndex < mid && rightIndex < right) {
            @SuppressWarnings("unchecked")
            T leftValue = (T) buffer[leftIndex];
            @SuppressWarnings("unchecked")
            T rightValue = (T) buffer[rightIndex];

            if (comparator.compare(leftValue, rightValue) <= 0) {
                values[destIndex++] = leftValue;
                leftIndex++;
            } else {
                values[destIndex++] = rightValue;
                rightIndex++;
            }
        }

        while (leftIndex < mid) {
            @SuppressWarnings("unchecked")
            T value = (T) buffer[leftIndex++];
            values[destIndex++] = value;
        }
    }

    public static void main(String[] args) {
        MergeSortDemo.main(args);
    }
}
