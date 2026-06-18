package graph.exception;

public class InvalidGraphException extends GraphException {
    public InvalidGraphException(String message) {
        super(message);
    }

    public InvalidGraphException(String message, Throwable cause) {
        super(message, cause);
    }
}
