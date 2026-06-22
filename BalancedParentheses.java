import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

public class BalancedParentheses {
    private static final String DEFAULT_SAMPLE = "[()()]{}";

    private BalancedParentheses() {}

    public static boolean isBalanced(String s) {
        return isBalanced((CharSequence) s);
    }

    public static boolean isBalanced(CharSequence input) {
        Objects.requireNonNull(input, "input");
        BalanceChecker checker = new BalanceChecker();

        for (int i = 0; i < input.length(); i++) {
            if (!checker.accept(input.charAt(i))) {
                return false;
            }
        }

        return checker.isBalanced();
    }

    public static void main(String[] args) {
        String s = args.length > 0 ? args[0] : DEFAULT_SAMPLE;
        System.out.println((isBalanced(s) ? "true" : "false"));
    }

    private static final class BalanceChecker {
        private final Deque<Character> expectedClosings = new ArrayDeque<>();

        private boolean accept(char candidate) {
            BracketToken token = BracketToken.from(candidate);
            if (token == null) {
                return true;
            }

            if (token.isOpening()) {
                expectedClosings.push(token.expectedClosing());
                return true;
            }

            return !expectedClosings.isEmpty() && expectedClosings.pop() == token.expectedClosing();
        }

        private boolean isBalanced() {
            return expectedClosings.isEmpty();
        }
    }

    private enum BracketToken {
        OPEN_PAREN(')', true),
        OPEN_BRACE('}', true),
        OPEN_BRACKET(']', true),
        CLOSE_PAREN(')', false),
        CLOSE_BRACE('}', false),
        CLOSE_BRACKET(']', false);

        private final char expectedClosing;
        private final boolean opening;

        BracketToken(char expectedClosing, boolean opening) {
            this.expectedClosing = expectedClosing;
            this.opening = opening;
        }

        private static BracketToken from(char candidate) {
            return switch (candidate) {
                case '(' -> OPEN_PAREN;
                case '{' -> OPEN_BRACE;
                case '[' -> OPEN_BRACKET;
                case ')' -> CLOSE_PAREN;
                case '}' -> CLOSE_BRACE;
                case ']' -> CLOSE_BRACKET;
                default -> null;
            };
        }

        private boolean isOpening() {
            return opening;
        }

        private char expectedClosing() {
            return expectedClosing;
        }
    }
}
