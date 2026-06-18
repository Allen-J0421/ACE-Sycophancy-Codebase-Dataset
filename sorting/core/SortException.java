package sorting.core;

public sealed class SortException extends Exception permits
        SortValidationException,
        SortExecutionException,
        SortConfigurationException {

    protected SortException(String message) {
        super(message);
    }

    protected SortException(String message, Throwable cause) {
        super(message, cause);
    }
}
