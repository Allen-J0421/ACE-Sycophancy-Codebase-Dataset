import sorting.analysis.MetricsCollector;
import sorting.analysis.SortMetrics;
import sorting.core.SortException;
import sorting.core.SortOperation;
import sorting.core.SortValidationException;
import sorting.core.SortExecutionException;
import sorting.event.SortEventPublisher;
import sorting.event.SortingEvent;
import sorting.strategy.SortingStrategy;
import sorting.adapter.JavaUtilAdapter;

import java.util.Comparator;

public class ModularDemo {
    public static void main(String[] args) {
        demonstrateExceptionHierarchy();
        demonstrateOperations();
        demonstrateMetrics();
        demonstrateEventPublishing();
        demonstrateAdapter();
    }

    private static void demonstrateExceptionHierarchy() {
        System.out.println("=== SEALED EXCEPTION HIERARCHY ===\n");

        try {
            throw new SortValidationException("Array size invalid");
        } catch (SortException e) {
            System.out.println("Caught: " + e.getClass().getSimpleName());
            System.out.println("Message: " + e.getMessage());
        }

        try {
            throw new SortExecutionException("Runtime error occurred");
        } catch (SortException e) {
            System.out.println("Caught: " + e.getClass().getSimpleName());
        }

        System.out.println();
    }

    private static void demonstrateOperations() {
        System.out.println("=== FUNCTIONAL SORT OPERATIONS ===\n");

        Integer[] data = {38, 27, 43, 10};

        SortOperation<Integer> validation = (array, comp) -> {
            if (array == null || array.length == 0) {
                throw new SortValidationException("Invalid array");
            }
        };

        SortOperation<Integer> sorting = (array, comp) -> {
            System.out.println("Executing sort on array of size " + array.length);
        };

        try {
            SortOperation<Integer> combined = validation.andThen(sorting);
            combined.execute(data, Comparable::compareTo);
        } catch (SortException e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println();
    }

    private static void demonstrateMetrics() {
        System.out.println("=== ADVANCED METRICS ===\n");

        MetricsCollector collector = new MetricsCollector();

        for (int i = 0; i < 5; i++) {
            SortMetrics metrics = new SortMetrics.Builder()
                    .durationNanos(1_000_000L + (i * 100_000L))
                    .comparisons(20 + i)
                    .swaps(25 + i)
                    .arraySize(100)
                    .algorithmName("MergeSort")
                    .build();
            collector.record(metrics);
        }

        System.out.println(collector.generateReport());
        System.out.println();
    }

    private static void demonstrateEventPublishing() {
        System.out.println("=== EVENT PUBLISHING SYSTEM ===\n");

        SortEventPublisher.DefaultPublisher publisher = new SortEventPublisher.DefaultPublisher();

        publisher.subscribe(event -> {
            System.out.println("[Event] " + event.type().getDescription() + ": " + event.message());
        });

        publisher.subscribe(event -> {
            if (event.type() == SortingEvent.EventType.SORT_COMPLETED) {
                System.out.println("[Alert] Sort completed successfully!");
            }
        });

        publisher.publish(SortingEvent.started("Sorting array"));
        publisher.publish(SortingEvent.completed("Array sorted"));
        publisher.publish(SortingEvent.metricsRecorded("Metrics saved"));

        System.out.println("\nTotal subscribers: " + publisher.subscriberCount());
        System.out.println();
    }

    private static void demonstrateAdapter() {
        System.out.println("=== JAVA.UTIL ADAPTER ===\n");

        Integer[] data = {38, 27, 43, 10, 5, 99};

        System.out.println("Before: " + formatArray(data));
        System.out.println("Is sorted: " + JavaUtilAdapter.isSorted(data));

        JavaUtilAdapter.shuffle(data);
        System.out.println("After shuffle: " + formatArray(data));

        int index = JavaUtilAdapter.binarySearch(data, 43);
        System.out.println("Binary search for 43: index = " + index);

        System.out.println();
    }

    private static <T> String formatArray(T[] array) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < Math.min(array.length, 8); i++) {
            if (i > 0) sb.append(", ");
            sb.append(array[i]);
        }
        if (array.length > 8) sb.append(", ...");
        sb.append("]");
        return sb.toString();
    }
}

