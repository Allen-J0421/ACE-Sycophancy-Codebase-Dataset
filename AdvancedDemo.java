import java.util.Comparator;

public class AdvancedDemo {
    public static void main(String[] args) {
        demonstrateProfiles();
        demonstrateStrategies();
        demonstrateBenchmarking();
    }

    private static void demonstrateProfiles() {
        System.out.println("=== SORT PROFILES DEMONSTRATION ===\n");

        Integer[] testData = {38, 27, 43, 10, 5, 99, 12};

        for (SortProfile profile : SortProfile.values()) {
            System.out.println("Profile: " + profile.name());
            System.out.println("Description: " + profile.description());

            SortConfiguration<Integer> config = profile.create();
            OptimizedMergeSort<Integer> sorter = new OptimizedMergeSort<>(config);
            Integer[] array = testData.clone();
            sorter.sort(array);

            printArray("Result: ", array);
            if (sorter.getStatistics() != null) {
                System.out.println("Statistics: " + sorter.getStatistics());
            }
            System.out.println();
        }
    }

    private static void demonstrateStrategies() {
        System.out.println("\n=== SORT STRATEGY DEMONSTRATION ===\n");

        Integer[] testData = {38, 27, 43, 10, 5, 99, 12, 8, 3, 50};

        SortConfiguration<Integer> config = SortProfile.BALANCED.create();

        System.out.println("Using Insertion Sort Strategy:");
        SortStrategy<Integer> insertionStrategy = new InsertionSortStrategy<>(Comparable::compareTo, null);
        OptimizedMergeSort<Integer> sorterInsertion = new OptimizedMergeSort<>(config, insertionStrategy);
        Integer[] array1 = testData.clone();
        sorterInsertion.sort(array1);
        printArray("Result: ", array1);
        if (sorterInsertion.getStatistics() != null) {
            System.out.println("Statistics: " + sorterInsertion.getStatistics());
        }

        System.out.println("\nUsing Heap Sort Strategy:");
        SortStrategy<Integer> heapStrategy = new HeapSortStrategy<>(null);
        OptimizedMergeSort<Integer> sorterHeap = new OptimizedMergeSort<>(config, heapStrategy);
        Integer[] array2 = testData.clone();
        sorterHeap.sort(array2);
        printArray("Result: ", array2);
        if (sorterHeap.getStatistics() != null) {
            System.out.println("Statistics: " + sorterHeap.getStatistics());
        }
    }

    private static void demonstrateBenchmarking() {
        System.out.println("\n=== BENCHMARKING DEMONSTRATION ===\n");

        Integer[] largeArray = generateRandomArray(500);

        Sorter<Integer> sorter1 = new OptimizedMergeSort<Integer>(SortProfile.SPEED.create());
        Sorter<Integer> sorter2 = new OptimizedMergeSort<Integer>(SortProfile.BALANCED.create());

        SortBenchmark<Integer> bench1 = new SortBenchmark<>(sorter1, "OptimizedMergeSort (SPEED)");
        SortBenchmark<Integer> bench2 = new SortBenchmark<>(sorter2, "OptimizedMergeSort (BALANCED)");

        System.out.println("Benchmarking with 500 elements, 5 iterations:");
        System.out.println(bench1.benchmark(largeArray, 5));
        System.out.println(bench2.benchmark(largeArray, 5));
    }

    private static Integer[] generateRandomArray(int size) {
        Integer[] array = new Integer[size];
        for (int i = 0; i < size; i++) {
            array[i] = (int) (Math.random() * 10000);
        }
        return array;
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
