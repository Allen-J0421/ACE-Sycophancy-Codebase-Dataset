public final class QuickSortDemo {
    private static final int[] SAMPLE_VALUES = {10, 7, 8, 9, 1, 5};

    private QuickSortDemo() {
    }

    public static void main(String[] args) {
        int[] values = QuickSort.sortedCopy(SAMPLE_VALUES);

        System.out.print(IntArrayFormatter.format(values));
    }
}
