import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Set;

final class BalancedParentheses {
    private static final Set<Character> OPENING_BRACKETS = Set.of('(', '{', '[');
    private static final Map<Character, Character> MATCHING_OPENING_BRACKETS = Map.of(
        ')', '(',
        '}', '{',
        ']', '['
    );

    private BalancedParentheses() {
    }

    public static boolean isBalanced(String input) {
        Deque<Character> openBrackets = new ArrayDeque<>();

        for (char current : input.toCharArray()) {
            if (isOpeningBracket(current)) {
                openBrackets.push(current);
            } else {
                Character expectedOpening = expectedOpeningBracket(current);
                if (expectedOpening != null) {
                    if (openBrackets.isEmpty()) {
                        return false;
                    }

                    char opening = openBrackets.pop();
                    if (opening != expectedOpening) {
                        return false;
                    }
                }
            }
        }

        return openBrackets.isEmpty();
    }

    private static boolean isOpeningBracket(char c) {
        return OPENING_BRACKETS.contains(c);
    }

    private static Character expectedOpeningBracket(char closing) {
        return MATCHING_OPENING_BRACKETS.get(closing);
    }

    public static void main(String[] args) {
        String sample = "[()()]{}";
        System.out.println(isBalanced(sample));
    }
}
