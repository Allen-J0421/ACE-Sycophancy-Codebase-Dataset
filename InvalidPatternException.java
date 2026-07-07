public class InvalidPatternException extends PatternSearchException {
    public InvalidPatternException(String pattern) {
        super("Pattern must be non-empty, got: " + (pattern == null ? "null" : "\"" + pattern + "\""));
    }
}
