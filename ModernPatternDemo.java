import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ModernPatternDemo {
    public static void main(String[] args) {
        demonstrateRecordsAndSealed();
        demonstratePatternMatching();
        demonstrateStreamAnalysis();
        demonstrateModernListeners();
    }

    private static void demonstrateRecordsAndSealed() {
        System.out.println("=== RECORDS & SEALED CLASSES DEMONSTRATION ===\n");

        Integer[] data = {38, 27, 43, 10, 5, 99};
        ModernMergeSort<Integer> sorter = new ModernMergeSort<Integer>(
                SortProfile.BALANCED.create());

        sorter.sort(data);

        SortResult result = new SortResult(
                1_500_000L,
                20L,
                25L,
                true,
                "Test sort completed");

        System.out.println("Result Record: " + result);
        System.out.println("Duration: " + result.durationMs() + " ms");
        System.out.println("Comparisons per element: " + result.comparisonsPerElement(data.length));
        System.out.println();
    }

    private static void demonstratePatternMatching() {
        System.out.println("=== PATTERN MATCHING DEMONSTRATION ===\n");

        List<SortEventType> events = new ArrayList<>();
        events.add(new SortStartedEvent("Sorting small array"));
        events.add(new SortCompletedEvent("Sort finished",
                new SortResult(2_000_000L, 15L, 20L, true, "Success")));
        events.add(new SortFailedEvent("Sort failed", new RuntimeException("Memory error")));

        for (SortEventType event : events) {
            String info = switch (event) {
                case SortStartedEvent e -> "Started: " + e.message();
                case SortCompletedEvent e -> "Completed in " + e.result().durationMs() + "ms";
                case SortFailedEvent e -> "Failed with: " + e.cause().getMessage();
                case ValidationFailedEvent e -> "Validation error: " + e.message();
            };
            System.out.println(info);
        }
        System.out.println();
    }

    private static void demonstrateStreamAnalysis() {
        System.out.println("=== STREAM & ANALYSIS API DEMONSTRATION ===\n");

        // Simulate multiple sort runs
        List<SortResult> runs = new ArrayList<>();
        runs.add(new SortResult(1_500_000L, 20L, 25L, true, "Run 1"));
        runs.add(new SortResult(1_600_000L, 21L, 26L, true, "Run 2"));
        runs.add(new SortResult(1_450_000L, 19L, 24L, true, "Run 3"));
        runs.add(new SortResult(1_700_000L, 22L, 27L, true, "Run 4"));

        SortAnalyzer analyzer = new SortAnalyzer();
        analyzer.recordMultiple(runs);

        System.out.println(analyzer.summary());
        System.out.println("\nFastest run: " + analyzer.fastest());
        System.out.println("Slowest run: " + analyzer.slowest());
        System.out.println();
    }

    private static void demonstrateModernListeners() {
        System.out.println("=== MODERN LISTENERS WITH PATTERN MATCHING ===\n");

        Integer[] data = {38, 27, 43, 10};

        ModernMergeSort<Integer> sorter = new ModernMergeSort<Integer>(
                SortProfile.BALANCED.create());

        // Console listener using pattern matching
        sorter.addListener(ModernSortListener.console());

        // Custom listener using lambda
        sorter.addListener(ModernSortListener.of(event -> {
            if (event instanceof SortCompletedEvent completed) {
                System.out.println("[STATS] Comparisons: " + completed.result().comparisons());
            }
        }));

        Integer[] arr = data.clone();
        sorter.sort(arr);
        printArray("Sorted: ", arr);
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
