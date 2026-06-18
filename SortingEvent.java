/**
 * Represents a sorting lifecycle event.
 * Provides detailed information about sorting operations and metrics.
 */
abstract class SortingEvent {
    private final long timestamp;
    private final String eventType;
    private final Object source;

    protected SortingEvent(String eventType, Object source) {
        this.timestamp = System.nanoTime();
        this.eventType = eventType;
        this.source = source;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getEventType() {
        return eventType;
    }

    public Object getSource() {
        return source;
    }

    @Override
    public String toString() {
        return String.format("%s [%s] at %d ns", eventType, source.getClass().getSimpleName(), timestamp);
    }

    /**
     * Sort started event.
     */
    static class SortStarted extends SortingEvent {
        private final int arraySize;

        public SortStarted(Object source, int arraySize) {
            super("SORT_STARTED", source);
            this.arraySize = arraySize;
        }

        public int getArraySize() {
            return arraySize;
        }
    }

    /**
     * Sort completed event.
     */
    static class SortCompleted extends SortingEvent {
        private final int arraySize;
        private final long durationNanos;
        private final long comparisons;
        private final long swaps;

        public SortCompleted(Object source, int arraySize, long durationNanos, long comparisons, long swaps) {
            super("SORT_COMPLETED", source);
            this.arraySize = arraySize;
            this.durationNanos = durationNanos;
            this.comparisons = comparisons;
            this.swaps = swaps;
        }

        public int getArraySize() {
            return arraySize;
        }

        public long getDurationNanos() {
            return durationNanos;
        }

        public long getDurationMillis() {
            return durationNanos / 1_000_000;
        }

        public long getComparisons() {
            return comparisons;
        }

        public long getSwaps() {
            return swaps;
        }
    }

    /**
     * Sort error event.
     */
    static class SortFailed extends SortingEvent {
        private final Exception exception;
        private final String errorMessage;

        public SortFailed(Object source, Exception exception) {
            super("SORT_FAILED", source);
            this.exception = exception;
            this.errorMessage = exception.getMessage();
        }

        public Exception getException() {
            return exception;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    /**
     * Configuration changed event.
     */
    static class ConfigurationChanged extends SortingEvent {
        private final String configKey;
        private final Object oldValue;
        private final Object newValue;

        public ConfigurationChanged(Object source, String configKey, Object oldValue, Object newValue) {
            super("CONFIG_CHANGED", source);
            this.configKey = configKey;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        public String getConfigKey() {
            return configKey;
        }

        public Object getOldValue() {
            return oldValue;
        }

        public Object getNewValue() {
            return newValue;
        }
    }

    /**
     * Service registered event.
     */
    static class ServiceRegistered extends SortingEvent {
        private final String serviceName;
        private final Class<?> serviceType;

        public ServiceRegistered(Object source, String serviceName, Class<?> serviceType) {
            super("SERVICE_REGISTERED", source);
            this.serviceName = serviceName;
            this.serviceType = serviceType;
        }

        public String getServiceName() {
            return serviceName;
        }

        public Class<?> getServiceType() {
            return serviceType;
        }
    }
}
