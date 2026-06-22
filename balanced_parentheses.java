import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

final class BalancedParentheses {
    private static final Map<Character, Character> DEFAULT_OPENING_BY_CLOSING = Map.of(
        ')', '(',
        '}', '{',
        ']', '['
    );

    private BalancedParentheses() {
    }

    public static boolean isBalanced(String input) {
        return isBalanced(input, DEFAULT_OPENING_BY_CLOSING);
    }

    public static boolean isBalanced(String input, Map<Character, Character> openingByClosing) {
        Deque<Character> openBrackets = new ArrayDeque<>();
        Set<Character> openingBrackets = new HashSet<>(openingByClosing.values());

        for (char current : input.toCharArray()) {
            if (isOpeningBracket(current, openingBrackets)) {
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

    private static boolean isOpeningBracket(char c, Set<Character> openingBrackets) {
        return openingBrackets.contains(c);
    }

    public static void main(String[] args) {
        String sample = "[()()]{}";
        System.out.println(isBalanced(sample));
    }
}
