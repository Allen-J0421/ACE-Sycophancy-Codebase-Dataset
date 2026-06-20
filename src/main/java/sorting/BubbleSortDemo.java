package sorting;

public final class BubbleSortDemo {
    private static final String SORTED_ARRAY_LABEL = "Sorted array: ";

    private BubbleSortDemo() {
    }

    public static void main(String[] args) {
        System.out.println(SORTED_ARRAY_LABEL);
        IntArrayFormatter.print(BubbleSort.sortedCopy(SortingExamples.demoScenario().input()));
    }
}
