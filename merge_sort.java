import java.util.Comparator;
import sorting.core.SortObserver;
import sorting.core.Sorter;
import sorting.algorithm.MergeSort;

class Main {
    private static <T> void printArray(T[] arr) {
        for (T x : arr)
            System.out.print(x + " ");
        System.out.println();
    }

    public static void main(String[] args) {
        SortObserver logger = new SortObserver() {
            public void onSortStart() {
                System.out.println("[sort started]");
            }
            public void onSortComplete(long durationNs, long comparisons) {
                System.out.printf("[done: %d ns, %d comparisons]%n", durationNs, comparisons);
            }
        };

        Integer[] ints = {38, 27, 43, 10};
        Sorter.<Integer>builder()
            .strategy(MergeSort::new)
            .comparator(Comparator.naturalOrder())
            .observer(logger)
            .build()
            .sort(ints);
        printArray(ints);

        Integer[] intsDesc = {38, 27, 43, 10};
        Sorter.<Integer>builder()
            .strategy(MergeSort::new)
            .comparator(Comparator.reverseOrder())
            .observer(logger)
            .build()
            .sort(intsDesc);
        printArray(intsDesc);

        String[] words = {"Banana", "apple", "Cherry", "date"};
        Sorter.<String>builder()
            .strategy(MergeSort::new)
            .comparator(String.CASE_INSENSITIVE_ORDER)
            .observer(logger)
            .build()
            .sort(words);
        printArray(words);
    }
}
