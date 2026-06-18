import java.util.Objects;

public final class QuickSort {

    private QuickSort() {
    }

    public static void sort(int[] values) {
        Objects.requireNonNull(values, "values");
        sort(values, 0, values.length);
    }

    public static void sort(int[] values, int fromIndex, int toIndex) {
        Objects.requireNonNull(values, "values");
        validateRange(values.length, fromIndex, toIndex);

        if (toIndex - fromIndex < 2) {
            return;
        }

        QuickSortAlgorithm.sort(values, fromIndex, toIndex);
    }

    public static int[] sortedCopy(int[] values) {
        Objects.requireNonNull(values, "values");

        int[] copy = values.clone();
        sort(copy);
        return copy;
    }

    private static void validateRange(int length, int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex > length) {
            throw new IndexOutOfBoundsException(
                    "Range [" + fromIndex + ", " + toIndex + ") out of bounds for length " + length);
        }

        if (fromIndex > toIndex) {
            throw new IllegalArgumentException(
                    "fromIndex (" + fromIndex + ") must be <= toIndex (" + toIndex + ")");
        }
    }
}
