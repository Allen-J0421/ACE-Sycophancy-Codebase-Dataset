import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Set;

public class BalancedParentheses {
    private static final Map<Character, Character> MATCHING_OPENERS = Map.of(
        ')', '(',
        '}', '{',
        ']', '['
    );
    private static final Set<Character> OPENING_BRACKETS = Set.copyOf(MATCHING_OPENERS.values());
    private static final String DEFAULT_SAMPLE = "[()()]{}";

    private BalancedParentheses() {}

    public static boolean isBalanced(String s) {
        Deque<Character> brackets = new ArrayDeque<>();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (OPENING_BRACKETS.contains(c)) {
                brackets.push(c);
                continue;
            }

            if (MATCHING_OPENERS.containsKey(c)) {
                if (brackets.isEmpty() || !hasExpectedOpeningBracket(brackets.pop(), c)) {
                    return false;
                }
            }
        }

        return brackets.isEmpty();
    }

    private static boolean hasExpectedOpeningBracket(char openingBracket, char closingBracket) {
        return MATCHING_OPENERS.get(closingBracket) == openingBracket;
    }

    public static void main(String[] args) {
        String s = args.length > 0 ? args[0] : DEFAULT_SAMPLE;
        System.out.println((isBalanced(s) ? "true" : "false"));
    }
}
