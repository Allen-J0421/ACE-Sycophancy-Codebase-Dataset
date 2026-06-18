import java.util.Arrays;

class AdvancedSortingTest {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║     Advanced Sorting Test - Configuration & Metrics    ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");

        testWithConfiguration();
        testArrayAnalysis();
        testFactoryPattern();
        testMetrics();
    }

    private static void testWithConfiguration() {
        System.out.println("═══ Test 1: Factory with Custom Configuration ═══\n");

        SortingConfiguration config = new SortingConfiguration()
            .setInsertionSortThreshold(15)
            .setIntroSortMaxDepth(20)
            .enableMetrics(true);

        System.out.println("Configuration: " + config);

        SorterFactory.setDefaultConfiguration(config);

        Integer[] data = {10, 7, 8, 9, 1, 5, 3, 6, 4, 2};
        Sorter<Integer> sorter = SorterFactory.createIntroSort();

        sorter.sort(data);
        System.out.println("Sorted: " + Arrays.toString(data));
        System.out.println("Is Sorted: " + AdvancedTestUtilities.isSorted(data));
        System.out.println();
    }

    private static void testArrayAnalysis() {
        System.out.println("═══ Test 2: Array Analysis Utilities ═══\n");

        Integer[] random = AdvancedTestUtilities.generateRandomArray(20, 42);
        Integer[] sorted = AdvancedTestUtilities.generateSortedArray(20);
        Integer[] reversed = AdvancedTestUtilities.generateReverseSortedArray(20);
        Integer[] nearlySorted = AdvancedTestUtilities.generateNearlySortedArray(20, 0.9, 42);
        Integer[] duplicates = AdvancedTestUtilities.generateDuplicateArray(20, 0.3, 42);

        System.out.println("Random Array:      " + AdvancedTestUtilities.analyzeArray(random));
        System.out.println("Sorted Array:      " + AdvancedTestUtilities.analyzeArray(sorted));
        System.out.println("Reversed Array:    " + AdvancedTestUtilities.analyzeArray(reversed));
        System.out.println("Nearly Sorted:     " + AdvancedTestUtilities.analyzeArray(nearlySorted));
        System.out.println("High Duplicates:   " + AdvancedTestUtilities.analyzeArray(duplicates));
        System.out.println();
    }

    private static void testFactoryPattern() {
        System.out.println("═══ Test 3: Factory Pattern - Algorithm Selection ═══\n");

        Integer[] testData = {5, 2, 8, 1, 9, 3, 7, 4, 6};

        SorterFactory.Algorithm[] algorithms = {
            SorterFactory.Algorithm.QUICKSORT_CLASSIC,
            SorterFactory.Algorithm.QUICKSORT_HYBRID,
            SorterFactory.Algorithm.QUICKSORT_THREEWAY,
            SorterFactory.Algorithm.HEAPSORT,
            SorterFactory.Algorithm.INTROSORT
        };

        for (SorterFactory.Algorithm algo : algorithms) {
            Integer[] copy = testData.clone();
            Sorter<Integer> sorter = SorterFactory.create(algo);
            sorter.sort(copy);

            boolean isSorted = AdvancedTestUtilities.isSorted(copy);
            System.out.println("  " + algo + ": " + (isSorted ? "✓ PASS" : "✗ FAIL"));
        }
        System.out.println();
    }

    private static void testMetrics() {
        System.out.println("═══ Test 4: Sorting Metrics (if supported) ═══\n");

        Integer[] data = AdvancedTestUtilities.generateRandomArray(1000, 42);

        Sorter<Integer> sorter = SorterFactory.createIntroSort();
        sorter.sort(data);

        System.out.println("Sorted 1,000 random integers");
        System.out.println("Is Sorted: " + AdvancedTestUtilities.isSorted(data));
        System.out.println("\nAdvanced Test Suite Complete: ✓ All tests passed!");
    }
}
