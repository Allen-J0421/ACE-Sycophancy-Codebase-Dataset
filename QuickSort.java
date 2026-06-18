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
        IndexRange range = IndexRange.of(values.length, fromIndex, toIndex);

        if (range.length() < 2) {
            return;
        }

        QuickSortAlgorithm.sort(values, range);
    }

    public static int[] sortedCopy(int[] values) {
        Objects.requireNonNull(values, "values");

        int[] copy = values.clone();
        sort(copy);
        return copy;
    }
}
