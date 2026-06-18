/**
 * Observer interface for sorting events.
 * Implementations can monitor and react to sorting lifecycle events.
 */
interface EventObserver {
    /**
     * Called when a sorting event occurs.
     */
    void onEvent(SortingEvent event);

    /**
     * Gets observer name for identification.
     */
    String getName();

    /**
     * Checks if observer is interested in this event type.
     */
    default boolean isInterestedIn(String eventType) {
        return true;  // By default, interested in all events
    }
}

/**
 * Logging observer that logs events to System.out.
 */
class LoggingObserver implements EventObserver {
    private final String name;
    private final boolean verbose;

    public LoggingObserver(String name, boolean verbose) {
        this.name = name;
        this.verbose = verbose;
    }

    public LoggingObserver() {
        this("Logging", false);
    }

    @Override
    public void onEvent(SortingEvent event) {
        if (event instanceof SortingEvent.SortStarted) {
            SortingEvent.SortStarted e = (SortingEvent.SortStarted) event;
            System.out.println("[LOG] Sort started: " + e.getArraySize() + " elements");
        } else if (event instanceof SortingEvent.SortCompleted) {
            SortingEvent.SortCompleted e = (SortingEvent.SortCompleted) event;
            System.out.printf("[LOG] Sort completed in %.3f ms (%d comparisons, %d swaps)%n",
                e.getDurationMillis(), e.getComparisons(), e.getSwaps());
        } else if (event instanceof SortingEvent.SortFailed) {
            SortingEvent.SortFailed e = (SortingEvent.SortFailed) event;
            System.out.println("[LOG] Sort failed: " + e.getErrorMessage());
        } else if (event instanceof SortingEvent.ConfigurationChanged && verbose) {
            SortingEvent.ConfigurationChanged e = (SortingEvent.ConfigurationChanged) event;
            System.out.printf("[LOG] Config changed: %s = %s (was %s)%n",
                e.getConfigKey(), e.getNewValue(), e.getOldValue());
        } else if (event instanceof SortingEvent.ServiceRegistered && verbose) {
            SortingEvent.ServiceRegistered e = (SortingEvent.ServiceRegistered) event;
            System.out.println("[LOG] Service registered: " + e.getServiceName());
        }
    }

    @Override
    public String getName() {
        return name;
    }
}

/**
 * Metrics observer that collects performance statistics.
 */
class MetricsObserver implements EventObserver {
    private final String name;
    private long totalSorts = 0;
    private long totalTime = 0;
    private long totalComparisons = 0;
    private long totalSwaps = 0;
    private long failedSorts = 0;

    public MetricsObserver(String name) {
        this.name = name;
    }

    public MetricsObserver() {
        this("Metrics");
    }

    @Override
    public void onEvent(SortingEvent event) {
        if (event instanceof SortingEvent.SortCompleted) {
            SortingEvent.SortCompleted e = (SortingEvent.SortCompleted) event;
            totalSorts++;
            totalTime += e.getDurationNanos();
            totalComparisons += e.getComparisons();
            totalSwaps += e.getSwaps();
        } else if (event instanceof SortingEvent.SortFailed) {
            failedSorts++;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public long getTotalSorts() {
        return totalSorts;
    }

    public long getAverageDurationMillis() {
        return totalSorts > 0 ? totalTime / totalSorts / 1_000_000 : 0;
    }

    public long getAverageComparisons() {
        return totalSorts > 0 ? totalComparisons / totalSorts : 0;
    }

    public long getAverageSwaps() {
        return totalSorts > 0 ? totalSwaps / totalSorts : 0;
    }

    public long getFailedSorts() {
        return failedSorts;
    }

    public void reset() {
        totalSorts = 0;
        totalTime = 0;
        totalComparisons = 0;
        totalSwaps = 0;
        failedSorts = 0;
    }

    public void printReport() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║         Sorting Metrics Report         ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.printf("Total sorts: %d%n", totalSorts);
        System.out.printf("Failed sorts: %d%n", failedSorts);
        System.out.printf("Average duration: %.3f ms%n", getAverageDurationMillis());
        System.out.printf("Average comparisons: %d%n", getAverageComparisons());
        System.out.printf("Average swaps: %d%n", getAverageSwaps());
    }
}

/**
 * Error observer that tracks and collects errors.
 */
class ErrorObserver implements EventObserver {
    private final String name;
    private int errorCount = 0;

    public ErrorObserver(String name) {
        this.name = name;
    }

    public ErrorObserver() {
        this("ErrorTracker");
    }

    @Override
    public void onEvent(SortingEvent event) {
        if (event instanceof SortingEvent.SortFailed) {
            errorCount++;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isInterestedIn(String eventType) {
        return "SORT_FAILED".equals(eventType);
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void reset() {
        errorCount = 0;
    }
}
