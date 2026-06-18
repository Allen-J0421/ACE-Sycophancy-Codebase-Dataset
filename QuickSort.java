public final class QuickSort {
    private static final IntSorter SORTER = new QuickSortIntSorter();

    private QuickSort() {
    }

    public static int[] sortedCopy(int[] values) {
        return SORTER.sortedCopy(values);
    }

    public static void sort(int[] values) {
        SORTER.sort(values);
    }

    public static void main(String[] args) {
        QuickSortDemo.main(args);
    }
}
