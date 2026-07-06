public class InvalidBracketException extends RuntimeException {
    private final int position;

    public InvalidBracketException(int position, String message) {
        super(message);
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
