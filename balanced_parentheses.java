import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

final class BalancedParentheses {
    private static final Set<Character> DEFAULT_OPENING_BRACKETS = Set.of('(', '{', '[');
    private static final Map<Character, Character> DEFAULT_OPENING_BY_CLOSING = Map.of(
        ')', '(',
        '}', '{',
        ']', '['
    );

    private BalancedParentheses() {
    }

    public static boolean isBalanced(String input) {
        return isBalanced(input, DEFAULT_OPENING_BY_CLOSING, DEFAULT_OPENING_BRACKETS);
    }

    public static boolean isBalanced(String input, Map<Character, Character> openingByClosing) {
        return isBalanced(input, openingByClosing, new HashSet<>(openingByClosing.values()));
    }

    private static boolean isBalanced(
        String input,
        Map<Character, Character> openingByClosing,
        Set<Character> openingBrackets
    ) {
        Deque<Character> openBrackets = new ArrayDeque<>();

        for (char current : input.toCharArray()) {
            if (openingBrackets.contains(current)) {
                openBrackets.push(current);
            } else {
                Character expectedOpening = openingByClosing.get(current);
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

    public static void main(String[] args) {
        String sample = "[()()]{}";
        System.out.println(isBalanced(sample));
    }
}
