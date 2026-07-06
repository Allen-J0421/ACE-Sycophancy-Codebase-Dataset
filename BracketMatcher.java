import java.util.Map;

record BracketMatcher(Map<Character, Character> closerToOpener) {

    boolean isOpener(char c) {
        return closerToOpener.containsValue(c);
    }

    boolean isCloser(char c) {
        return closerToOpener.containsKey(c);
    }

    boolean matches(char opener, char closer) {
        return opener == closerToOpener.getOrDefault(closer, '\0');
    }
}
