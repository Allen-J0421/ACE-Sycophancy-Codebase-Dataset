import java.util.ArrayDeque;
import java.util.Deque;

public class BalancedParentheses {
    private static final String DEFAULT_SAMPLE = "[()()]{}";

    private BalancedParentheses() {}

    public static boolean isBalanced(String s) {
        Deque<BracketPair> openBrackets = new ArrayDeque<>();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            BracketPair openingBracket = BracketPair.fromOpening(c);

            if (openingBracket != null) {
                openBrackets.push(openingBracket);
                continue;
            }

            BracketPair closingBracket = BracketPair.fromClosing(c);
            if (closingBracket != null) {
                if (openBrackets.isEmpty() || openBrackets.pop() != closingBracket) {
                    return false;
                }
            }
        }

        return openBrackets.isEmpty();
    }

    public static void main(String[] args) {
        String s = args.length > 0 ? args[0] : DEFAULT_SAMPLE;
        System.out.println((isBalanced(s) ? "true" : "false"));
    }

    private enum BracketPair {
        PARENTHESES('(', ')'),
        BRACES('{', '}'),
        BRACKETS('[', ']');

        private final char opening;
        private final char closing;

        BracketPair(char opening, char closing) {
            this.opening = opening;
            this.closing = closing;
        }

        private static BracketPair fromOpening(char candidate) {
            for (BracketPair pair : values()) {
                if (pair.opening == candidate) {
                    return pair;
                }
            }

            return null;
        }

        private static BracketPair fromClosing(char candidate) {
            for (BracketPair pair : values()) {
                if (pair.closing == candidate) {
                    return pair;
                }
            }

            return null;
        }
    }
}
