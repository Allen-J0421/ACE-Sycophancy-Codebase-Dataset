import java.util.Comparator;

public class PatternDemo {
    public static void main(String[] args) {
        demonstrateFactory();
        demonstrateObserver();
        demonstrateAdapter();
        demonstratePipeline();
    }

    private static void demonstrateFactory() {
        System.out.println("=== FACTORY PATTERN DEMONSTRATION ===\n");

        Integer[] data = {38, 27, 43, 10, 5, 99};

        System.out.println("Available algorithms: " + SorterFactory.getAvailableAlgorithms());

        Sorter<Integer> optimized = SorterFactory.createOptimized();
        Sorter<Integer> fast = SorterFactory.createFast();
        Sorter<Integer> safe = SorterFactory.createSafe();

        System.out.println("\nOptimized sorter:");
        Integer[] arr1 = data.clone();
        optimized.sort(arr1);
        printArray("Result: ", arr1);

        System.out.println("Fast sorter:");
        Integer[] arr2 = data.clone();
        fast.sort(arr2);
        printArray("Result: ", arr2);

        System.out.println("Safe sorter:");
        Integer[] arr3 = data.clone();
        safe.sort(arr3);
        printArray("Result: ", arr3);
        if (safe.getStatistics() != null) {
            System.out.println("Statistics: " + safe.getStatistics());
        }

        System.out.println();
    }

    private static void demonstrateObserver() {
        System.out.println("=== OBSERVER PATTERN DEMONSTRATION ===\n");

        Integer[] data = {38, 27, 43, 10};

        Sorter<Integer> baseSorter = SorterFactory.createOptimized();
        ObservableSorter<Integer> observableSorter = new ObservableSorter<>(baseSorter);

        observableSorter.addListener(new ConsoleListener());
        observableSorter.addListener(event -> {
            if (event.getType() == SortEvent.Type.SORT_COMPLETED) {
                System.out.println("[CUSTOM] Sort finished!");
            }
        });

        Integer[] arr = data.clone();
        observableSorter.sort(arr);
        printArray("Result: ", arr);
        System.out.println();
    }

    private static void demonstrateAdapter() {
        System.out.println("=== ADAPTER PATTERN DEMONSTRATION ===\n");

        Integer[] data = {38, 27, 43, 10, 5, 99};

        System.out.println("Before sorting: " + ArraysSortAdapter.isSorted(data));
        ArraysSortAdapter.sort(data);
        System.out.println("After sorting: " + ArraysSortAdapter.isSorted(data));
        printArray("Result: ", data);

        String[] words = {"dog", "cat", "elephant", "ant", "bear"};
        System.out.println("\nSorting words in descending order:");
        ArraysSortAdapter.sort(words, Comparator.reverseOrder());
        printArray("Result: ", words);
        System.out.println();
    }

    private static void demonstratePipeline() {
        System.out.println("=== PIPELINE PATTERN DEMONSTRATION ===\n");

        Integer[] data = {38, 27, 43, 10, 5, 99, 12, 8};

        SortPipeline<Integer> pipeline = new SortPipeline<>("Multi-Stage Sort");
        pipeline.addStage(SorterFactory.<Integer>createOptimized());
        pipeline.addStage(new ObservableSorter<>(SorterFactory.<Integer>createOptimized()));

        Integer[] arr = data.clone();
        pipeline.execute(arr);
        printArray("Final result: ", arr);
        System.out.println();
    }

    private static <T> void printArray(String label, T[] array) {
        System.out.print(label);
        for (int i = 0; i < Math.min(array.length, 10); i++) {
            System.out.print(array[i] + " ");
        }
        if (array.length > 10) {
            System.out.print("...");
        }
        System.out.println();
    }
}
