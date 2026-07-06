import java.util.ArrayDeque;
import java.util.Deque;

class ValidationContext {
    private record Entry(char opener, int position) {}

    private final BracketMatcher matcher;
    private final Deque<Entry> stack = new ArrayDeque<>();
    private BalancedParenthesesResult failure = null;

    ValidationContext(BracketMatcher matcher) {
        this.matcher = matcher;
    }

    boolean process(char c, int position) {
        if (matcher.isOpener(c)) {
            stack.push(new Entry(c, position));
            return true;
        }
        if (matcher.isCloser(c)) {
            if (stack.isEmpty()) {
                failure = BalancedParenthesesResult.failure(position,
                    "unexpected closing bracket '" + c + "'");
                return false;
            }
            if (!matcher.matches(stack.peek().opener(), c)) {
                failure = BalancedParenthesesResult.failure(position,
                    "mismatched bracket '" + c + "'");
                return false;
            }
            stack.pop();
        }
        return true;
    }

    BalancedParenthesesResult result() {
        if (failure != null) return failure;
        if (!stack.isEmpty()) {
            Entry unclosed = stack.peek();
            return BalancedParenthesesResult.failure(unclosed.position(),
                "unclosed bracket '" + unclosed.opener() + "'");
        }
        return BalancedParenthesesResult.success();
    }
}
