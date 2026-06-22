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
            } else if (isClosingBracket(current)) {
                if (openBrackets.isEmpty() || !isMatchingPair(openBrackets.pop(), current)) {
                    return false;
                }
            }
        }

        return openBrackets.isEmpty();
    }

    private static boolean isOpeningBracket(char c) {
        return OPENING_BRACKETS.contains(c);
    }

    private static boolean isClosingBracket(char c) {
        return MATCHING_OPENING_BRACKETS.containsKey(c);
    }

    private static boolean isMatchingPair(char opening, char closing) {
        Character expectedOpening = MATCHING_OPENING_BRACKETS.get(closing);
        return expectedOpening != null && expectedOpening == opening;
    }

    public static void main(String[] args) {
        String sample = "[()()]{}";
        System.out.println(isBalanced(sample));
    }
}
