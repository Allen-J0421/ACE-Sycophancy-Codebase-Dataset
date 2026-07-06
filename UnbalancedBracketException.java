public class UnbalancedBracketException extends InvalidBracketException {
    public UnbalancedBracketException(int position, String message) {
        super(position, message);
    }
}
