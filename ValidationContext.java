import java.util.ArrayDeque;
import java.util.Deque;

class ValidationContext {
    private final BracketMatcher matcher;
    private final Deque<Character> stack = new ArrayDeque<>();

    ValidationContext(BracketMatcher matcher) {
        this.matcher = matcher;
    }

    boolean process(char c) {
        if (matcher.isOpener(c)) { stack.push(c); return true; }
        if (matcher.isCloser(c)) {
            if (stack.isEmpty() || !matcher.matches(stack.peek(), c)) return false;
            stack.pop();
        }
        return true;
    }

    boolean isComplete() {
        return stack.isEmpty();
    }
}
