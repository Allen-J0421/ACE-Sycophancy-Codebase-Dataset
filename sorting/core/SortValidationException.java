package sorting.core;

public final class SortValidationException extends SortException {
    public SortValidationException(String message) {
        super(message);
    }

    public SortValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
