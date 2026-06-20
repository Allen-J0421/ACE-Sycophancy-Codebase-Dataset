public final class BubbleSortDemo {
    private static final int[] SAMPLE_VALUES = {64, 34, 25, 12, 22, 11, 90};
    private static final String SORTED_ARRAY_LABEL = "Sorted array: ";

    private BubbleSortDemo() {
    }

    public static void main(String[] args) {
        System.out.println(SORTED_ARRAY_LABEL);
        IntArrayFormatter.print(BubbleSort.sortedCopy(SAMPLE_VALUES));
    }
}
