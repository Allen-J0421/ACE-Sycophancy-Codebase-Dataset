package sorting.core;

public final class SortExecutionException extends SortException {
    public SortExecutionException(String message) {
        super(message);
    }

    public SortExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
