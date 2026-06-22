public record BracketToken(char character, char expectedClosing, boolean opening) {
    static BracketToken opening(char character, char expectedClosing) {
        return new BracketToken(character, expectedClosing, true);
    }

    static BracketToken closing(char character) {
        return new BracketToken(character, character, false);
    }

    boolean isOpening() {
        return opening;
    }
}
