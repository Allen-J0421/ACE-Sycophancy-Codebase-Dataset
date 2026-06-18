package sorting.event;

public record SortingEvent(
        SortingEvent.EventType type,
        String message,
        long timestamp) {

    public enum EventType {
        SORT_STARTED("Sort operation started"),
        SORT_COMPLETED("Sort operation completed"),
        SORT_FAILED("Sort operation failed"),
        METRICS_RECORDED("Metrics recorded");

        private final String description;

        EventType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public static SortingEvent started(String message) {
        return new SortingEvent(EventType.SORT_STARTED, message, System.currentTimeMillis());
    }

    public static SortingEvent completed(String message) {
        return new SortingEvent(EventType.SORT_COMPLETED, message, System.currentTimeMillis());
    }

    public static SortingEvent failed(String message) {
        return new SortingEvent(EventType.SORT_FAILED, message, System.currentTimeMillis());
    }

    public static SortingEvent metricsRecorded(String message) {
        return new SortingEvent(EventType.METRICS_RECORDED, message, System.currentTimeMillis());
    }
}
