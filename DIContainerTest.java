import java.util.Arrays;

class DIContainerTest {
    private static int passedTests = 0;
    private static int totalTests = 0;

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║     Dependency Injection Container Test Suite          ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");

        testContainerRegistration();
        testSorterResolution();
        testSingletonBehavior();
        testTransientBehavior();
        testSorterFunctionality();

        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║ Final Test Results: " + passedTests + "/" + totalTests + " passed");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.println(passedTests == totalTests ? "✓ All DI container tests passed!" : "✗ Some tests failed!");
    }

    private static void testContainerRegistration() {
        System.out.println("═══ Test 1: Container Registration ═══\n");

        DIContainer container = new DIContainer();
        container.registerDefaults();

        boolean sortersRegistered = container.getRegistry().contains("QuickSortImpl")
            && container.getRegistry().contains("HybridQuickSort")
            && container.getRegistry().contains("IntroSort");

        recordTest(sortersRegistered, "Default sorters registered");

        boolean pivotsRegistered = container.getRegistry().contains("MedianOfThree")
            && container.getRegistry().contains("Random");

        recordTest(pivotsRegistered, "Pivot selectors registered");

        boolean utilitiesRegistered = container.getRegistry().contains("Configuration")
            && container.getRegistry().contains("Metrics");

        recordTest(utilitiesRegistered, "Utilities registered");
        System.out.println();
    }

    private static void testSorterResolution() {
        System.out.println("═══ Test 2: Sorter Resolution ═══\n");

        DIContainer container = new DIContainer();
        container.registerDefaults();

        try {
            Sorter<Integer> qs = container.getSorter(SorterFactory.Algorithm.QUICKSORT_CLASSIC);
            recordTest(qs != null, "QuickSortImpl resolved");

            Sorter<Integer> hybrid = container.getSorter(SorterFactory.Algorithm.QUICKSORT_HYBRID);
            recordTest(hybrid != null, "HybridQuickSort resolved");

            Sorter<Integer> intro = container.getSorter(SorterFactory.Algorithm.INTROSORT);
            recordTest(intro != null, "IntroSort resolved");
        } catch (Exception e) {
            recordTest(false, "Sorter resolution failed: " + e.getMessage());
        }
        System.out.println();
    }

    private static void testSingletonBehavior() {
        System.out.println("═══ Test 3: Singleton Behavior ═══\n");

        DIContainer container = new DIContainer();
        container.registerDefaults();

        Sorter<Integer> sorter1 = container.getSorter(SorterFactory.Algorithm.INTROSORT);
        Sorter<Integer> sorter2 = container.getSorter(SorterFactory.Algorithm.INTROSORT);

        boolean sameSingleton = sorter1 == sorter2;
        recordTest(sameSingleton, "Same singleton instance returned");

        SortingConfiguration config1 = container.getConfiguration();
        SortingConfiguration config2 = container.getConfiguration();

        boolean differentTransient = config1 != config2;
        recordTest(differentTransient, "Different transient instances created");
        System.out.println();
    }

    private static void testTransientBehavior() {
        System.out.println("═══ Test 4: Transient Behavior ═══\n");

        DIContainer container = new DIContainer();
        container.registerDefaults();

        SortingMetrics metrics1 = container.getMetrics();
        SortingMetrics metrics2 = container.getMetrics();

        boolean differentInstances = metrics1 != metrics2;
        recordTest(differentInstances, "Transient metrics instances are different");

        SortingConfiguration config1 = container.getConfiguration();
        SortingConfiguration config2 = container.getConfiguration();

        boolean alsoTransient = config1 != config2;
        recordTest(alsoTransient, "Transient configuration instances are different");
        System.out.println();
    }

    private static void testSorterFunctionality() {
        System.out.println("═══ Test 5: Sorter Functionality ═══\n");

        DIContainer container = new DIContainer();
        container.registerDefaults();

        Integer[] testData = {10, 7, 8, 9, 1, 5, 3, 6, 4, 2};

        // Test QuickSortImpl
        Integer[] data1 = testData.clone();
        Sorter<Integer> qs = container.getSorter(SorterFactory.Algorithm.QUICKSORT_CLASSIC);
        qs.sort(data1);
        boolean qsSorted = isSorted(data1);
        recordTest(qsSorted, "QuickSortImpl from DI container works");

        // Test HybridQuickSort
        Integer[] data2 = testData.clone();
        Sorter<Integer> hybrid = container.getSorter(SorterFactory.Algorithm.QUICKSORT_HYBRID);
        hybrid.sort(data2);
        boolean hybridSorted = isSorted(data2);
        recordTest(hybridSorted, "HybridQuickSort from DI container works");

        // Test IntroSort
        Integer[] data3 = testData.clone();
        Sorter<Integer> intro = container.getSorter(SorterFactory.Algorithm.INTROSORT);
        intro.sort(data3);
        boolean introSorted = isSorted(data3);
        recordTest(introSorted, "IntroSort from DI container works");

        System.out.println();
    }

    private static boolean isSorted(Integer[] array) {
        for (int i = 1; i < array.length; i++) {
            if (array[i - 1] > array[i]) {
                return false;
            }
        }
        return true;
    }

    private static void recordTest(boolean passed, String testName) {
        totalTests++;
        if (passed) {
            passedTests++;
            System.out.println("    ✓ " + testName);
        } else {
            System.out.println("    ✗ " + testName);
        }
    }
}
