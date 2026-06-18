import sorting.core.*;
import sorting.analysis.ComparativeBenchmark;
import sorting.testing.PropertyTestGenerator;

import java.util.Comparator;

public class AdvancedFeaturesDemo {
    public static void main(String[] args) throws SortException, SortConfigurationException {
        demonstrateSortConfig();
        demonstrateParallelSorting();
        demonstrateCaching();
        demonstrativePropertyTesting();
        demonstrativeComparativeBenchmark();
    }

    private static void demonstrateSortConfig() throws SortConfigurationException {
        System.out.println("=== IMMUTABLE SORT CONFIGURATION ===\n");

        SortConfig<Integer> config = SortConfig.<Integer>builder()
                .comparator(Integer::compareTo)
                .insertionThreshold(20)
                .trackMetrics(true)
                .validateInput(true)
                .threadPoolSize(2)
                .build();

        System.out.println("Configuration:");
        System.out.println("  Insertion Threshold: " + config.insertionThreshold());
        System.out.println("  Track Metrics: " + config.trackMetrics());
        System.out.println("  Validate Input: " + config.validateInput());
        System.out.println("  Thread Pool Size: " + config.threadPoolSize());
        System.out.println("  Is Parallel: " + config.isParallel());
        System.out.println();
    }

    private static void demonstrateParallelSorting() throws SortException, SortConfigurationException {
        System.out.println("=== PARALLEL MERGE SORT ===\n");

        Integer[] data = generateTestData(1000);

        SortConfig<Integer> config = SortConfig.<Integer>builder()
                .comparator(Integer::compareTo)
                .insertionThreshold(50)
                .parallel()
                .build();

        ParallelMergeSort<Integer> sorter = new ParallelMergeSort<>(config);

        long start = System.nanoTime();
        sorter.sort(data);
        long duration = System.nanoTime() - start;
        sorter.shutdown();

        System.out.println("Sorted " + data.length + " elements in " +
                (duration / 1_000_000.0) + " ms");
        System.out.println("Is sorted: " + isSorted(data));
        System.out.println();
    }

    private static void demonstrateCaching() throws SortException, SortConfigurationException {
        System.out.println("=== SORT CACHING ===\n");

        SortConfig<Integer> config = SortConfig.<Integer>builder()
                .comparator(Integer::compareTo)
                .build();

        CachedSorter<Integer> cachedSorter = new CachedSorter<>(config, 5);

        Integer[] data1 = {5, 3, 8, 1};
        Integer[] data2 = {9, 2, 7, 4};

        System.out.println("Before any sorts: " + cachedSorter.getCacheStats());

        cachedSorter.sort(data1);
        cachedSorter.sort(data2);
        cachedSorter.sort(data1); // Cache hit

        System.out.println("After sorting: " + cachedSorter.getCacheStats());
        System.out.println();
    }

    private static void demonstrativePropertyTesting() throws SortException, SortConfigurationException {
        System.out.println("=== PROPERTY-BASED TESTING ===\n");

        PropertyTestGenerator<Integer> tester = new PropertyTestGenerator<>();

        SortConfig<Integer> config = SortConfig.<Integer>builder()
                .comparator(Integer::compareTo)
                .build();

        ParallelMergeSort<Integer> sorter = new ParallelMergeSort<>(config);

        // Test with random data
        Integer[] randomData = PropertyTestGenerator.TestDataGenerator.randomIntegers(100);
        tester.runPropertyTests(randomData, a -> {
            try {
                sorter.sort(a);
            } catch (SortException e) {
                e.printStackTrace();
            }
        }, 10);

        // Test with reversed data
        Integer[] reversedData = PropertyTestGenerator.TestDataGenerator.reversed(100);
        tester.runPropertyTests(reversedData, a -> {
            try {
                sorter.sort(a);
            } catch (SortException e) {
                e.printStackTrace();
            }
        }, 10);

        // Test with duplicates
        Integer[] duplicateData = PropertyTestGenerator.TestDataGenerator.duplicates(100);
        tester.runPropertyTests(duplicateData, a -> {
            try {
                sorter.sort(a);
            } catch (SortException e) {
                e.printStackTrace();
            }
        }, 10);

        sorter.shutdown();
        System.out.println();
    }

    private static void demonstrativeComparativeBenchmark() throws SortException, SortConfigurationException {
        System.out.println("=== COMPARATIVE BENCHMARKING ===\n");

        Integer[] testData = PropertyTestGenerator.TestDataGenerator.randomIntegers(5000);

        ComparativeBenchmark<Integer> benchmark = new ComparativeBenchmark<>();

        // Add sequential sort
        benchmark.addAlgorithm("Sequential MergeSort", (data, comp) -> {
            SortConfig<Integer> config = null;
            try {
                config = SortConfig.<Integer>builder()
                        .comparator(comp)
                        .threadPoolSize(1)
                        .build();
            } catch (SortConfigurationException e) {
                e.printStackTrace();
            }
            ParallelMergeSort<Integer> sorter = new ParallelMergeSort<>(config);
            long start = System.nanoTime();
            try {
                sorter.sort(data);
            } catch (SortException e) {
                e.printStackTrace();
            }
            long duration = System.nanoTime() - start;
            sorter.shutdown();
            return duration;
        });

        // Add parallel sort
        benchmark.addAlgorithm("Parallel MergeSort (4 threads)", (data, comp) -> {
            SortConfig<Integer> config = null;
            try {
                config = SortConfig.<Integer>builder()
                        .comparator(comp)
                        .threadPoolSize(4)
                        .build();
            } catch (SortConfigurationException e) {
                e.printStackTrace();
            }
            ParallelMergeSort<Integer> sorter = new ParallelMergeSort<>(config);
            long start = System.nanoTime();
            try {
                sorter.sort(data);
            } catch (SortException e) {
                e.printStackTrace();
            }
            long duration = System.nanoTime() - start;
            sorter.shutdown();
            return duration;
        });

        ComparativeBenchmark.BenchmarkResults results = benchmark.run(testData, 3);
        System.out.println(results.summary());
    }

    private static Integer[] generateTestData(int size) {
        Integer[] data = new Integer[size];
        for (int i = 0; i < size; i++) {
            data[i] = (int) (Math.random() * 100000);
        }
        return data;
    }

    private static boolean isSorted(Integer[] array) {
        for (int i = 0; i < array.length - 1; i++) {
            if (array[i] > array[i + 1]) {
                return false;
            }
        }
        return true;
    }
}
