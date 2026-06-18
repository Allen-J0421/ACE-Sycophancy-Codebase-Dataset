import sorting.core.*;
import sorting.analysis.Profiler;
import sorting.reactive.SortStream;
import sorting.spi.SortProvider;
import sorting.testing.PropertyTestGenerator;

public class FullStackDemo {
    public static void main(String[] args) throws SortException, SortConfigurationException {
        demonstrateAnnotations();
        demonstrateProfiling();
        demonstrateReactiveStreams();
        demonstrateOptimizationProfiles();
    }

    private static void demonstrateAnnotations() throws SortConfigurationException {
        System.out.println("=== ANNOTATION-DRIVEN CONFIGURATION ===\n");

        @Sortable(
                value = "UserData",
                algorithm = "parallel-merge-sort",
                enableMetrics = true,
                enableValidation = true,
                enableCaching = true,
                cacheSize = 50,
                insertionThreshold = 15
        )
        class UserData implements Comparable<UserData> {
            @Override
            public int compareTo(UserData o) {
                return 0;
            }
        }

        SortConfig<UserData> config = SortableProcessor.processSortable(UserData.class);
        System.out.println("Processed @Sortable annotation:");
        System.out.println("  Insertion Threshold: " + config.insertionThreshold());
        System.out.println("  Track Metrics: " + config.trackMetrics());
        System.out.println("  Validate Input: " + config.validateInput());
        System.out.println("  Thread Pool Size: " + config.threadPoolSize());
        System.out.println();
    }

    private static void demonstrateProfiling() throws SortException, SortConfigurationException {
        System.out.println("=== PERFORMANCE PROFILING ===\n");

        Profiler profiler = new Profiler("MergeSortProfiling");

        Integer[] data = PropertyTestGenerator.TestDataGenerator.randomIntegers(5000);

        SortConfig<Integer> config = OptimizationProfile.BALANCED.apply(SortConfig.<Integer>builder());
        ParallelMergeSort<Integer> sorter = new ParallelMergeSort<>(config);

        profiler.start("sort");
        long start = System.nanoTime();
        sorter.sort(data);
        long duration = System.nanoTime() - start;
        profiler.recordValue("sort", duration);
        sorter.shutdown();

        System.out.println(profiler.report());
    }

    private static void demonstrateReactiveStreams() {
        System.out.println("=== REACTIVE STREAMS ===\n");

        SortStream.ReactiveSort<Integer> reactiveSort = new SortStream.ReactiveSort<>(
                100,
                Integer::compareTo);

        reactiveSort.subscribeTo(sortedBatch -> {
            System.out.println("Processed batch of " + sortedBatch.length + " elements");
        });

        Integer[] batch1 = {5, 3, 8, 1, 9};
        Integer[] batch2 = {7, 2, 6, 4};

        reactiveSort.processBatch(batch1);
        reactiveSort.processBatch(batch2);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        reactiveSort.close();
        System.out.println();
    }

    private static void demonstrateOptimizationProfiles() throws SortConfigurationException {
        System.out.println("=== OPTIMIZATION PROFILES ===\n");

        Integer[] testData = PropertyTestGenerator.TestDataGenerator.randomIntegers(1000);

        for (OptimizationProfile profile : OptimizationProfile.values()) {
            System.out.println("Profile: " + profile.name());
            System.out.println("Description: " + profile.getDescription());

            SortConfig<Integer> config = profile.apply(SortConfig.<Integer>builder());
            System.out.println("  Insertion Threshold: " + config.insertionThreshold());
            System.out.println("  Track Metrics: " + config.trackMetrics());
            System.out.println("  Validate Input: " + config.validateInput());
            System.out.println("  Thread Pool Size: " + config.threadPoolSize());
            System.out.println();
        }
    }
}
