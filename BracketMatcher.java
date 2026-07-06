import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class BracketMatcher {
    private final Map<Character, Character> closerToOpener;
    private final Set<Character> openers;

    BracketMatcher(Map<Character, Character> closerToOpener) {
        this.closerToOpener = closerToOpener;
        this.openers = new HashSet<>(closerToOpener.values());
    }

    boolean isOpener(char c) {
        return openers.contains(c);
    }

    boolean isCloser(char c) {
        return closerToOpener.containsKey(c);
    }

    boolean matches(char opener, char closer) {
        return opener == closerToOpener.getOrDefault(closer, '\0');
    }
}
