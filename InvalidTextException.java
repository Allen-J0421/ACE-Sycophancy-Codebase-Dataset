public class InvalidTextException extends PatternSearchException {
    public InvalidTextException() {
        super("Search text must not be null");
    }
}
