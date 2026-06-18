package graph.exception;

public abstract class GraphException extends RuntimeException {
    protected GraphException(String message) {
        super(message);
    }

    protected GraphException(String message, Throwable cause) {
        super(message, cause);
    }
}
