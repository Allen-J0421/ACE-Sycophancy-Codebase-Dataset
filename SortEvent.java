public class SortEvent {
    public enum Type {
        SORT_STARTED,
        SORT_COMPLETED,
        SORT_FAILED,
        VALIDATION_FAILED
    }

    private final Type type;
    private final String message;
    private final long timestamp;
    private final Exception exception;

    public SortEvent(Type type, String message) {
        this(type, message, null);
    }

    public SortEvent(Type type, String message, Exception exception) {
        this.type = type;
        this.message = message;
        this.exception = exception;
        this.timestamp = System.currentTimeMillis();
    }

    public Type getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Exception getException() {
        return exception;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s at %d", type, message, timestamp);
    }
}
