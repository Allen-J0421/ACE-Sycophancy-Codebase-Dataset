import java.util.ArrayDeque;
import java.util.Deque;

class ValidationContext {
    private record Entry(char opener, int position) {}

    private final BracketMatcher matcher;
    private final Deque<Entry> stack = new ArrayDeque<>();

    ValidationContext(BracketMatcher matcher) {
        this.matcher = matcher;
    }

    void process(char c, int position) {
        if (matcher.isOpener(c)) {
            stack.push(new Entry(c, position));
            return;
        }
        if (matcher.isCloser(c)) {
            if (stack.isEmpty()) {
                throw new UnbalancedBracketException(position,
                    "unexpected closing bracket '" + c + "'");
            }
            if (!matcher.matches(stack.peek().opener(), c)) {
                throw new InvalidBracketException(position,
                    "mismatched bracket '" + c + "'");
            }
            stack.pop();
        }
    }

    void checkComplete() {
        if (!stack.isEmpty()) {
            Entry unclosed = stack.peek();
            throw new UnbalancedBracketException(unclosed.position(),
                "unclosed bracket '" + unclosed.opener() + "'");
        }
    }
}
