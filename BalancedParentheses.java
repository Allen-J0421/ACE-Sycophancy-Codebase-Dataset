import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

public class BalancedParentheses {
    private static final String DEFAULT_SAMPLE = "[()()]{}";

    private BalancedParentheses() {}

    public static boolean isBalanced(String s) {
        Objects.requireNonNull(s, "input");
        Deque<Character> expectedClosings = new ArrayDeque<>();

        for (int i = 0; i < s.length(); i++) {
            BracketToken token = BracketToken.from(s.charAt(i));
            if (token == null) {
                continue;
            }

            if (token.isOpening()) {
                expectedClosings.push(token.matchingCharacter());
                continue;
            }

            if (expectedClosings.isEmpty() || expectedClosings.pop() != token.character()) {
                return false;
            }
        }

        return expectedClosings.isEmpty();
    }

    public static void main(String[] args) {
        String s = args.length > 0 ? args[0] : DEFAULT_SAMPLE;
        System.out.println((isBalanced(s) ? "true" : "false"));
    }

    private record BracketToken(char character, char matchingCharacter, boolean opening) {
        private static BracketToken from(char candidate) {
            return switch (candidate) {
                case '(' -> new BracketToken(candidate, ')', true);
                case '{' -> new BracketToken(candidate, '}', true);
                case '[' -> new BracketToken(candidate, ']', true);
                case ')' -> new BracketToken(candidate, '(', false);
                case '}' -> new BracketToken(candidate, '{', false);
                case ']' -> new BracketToken(candidate, '[', false);
                default -> null;
            };
        }

        private boolean isOpening() {
            return opening;
        }
    }
}
