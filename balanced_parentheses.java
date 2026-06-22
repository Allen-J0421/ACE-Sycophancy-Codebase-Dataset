import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

final class BalancedParentheses {
    private static final Map<Character, Character> DEFAULT_PAIRS = Map.of(
        ')', '(',
        '}', '{',
        ']', '['
    );

    private BalancedParentheses() {
    }

    public static boolean isBalanced(String input) {
        return isBalanced(input, DEFAULT_PAIRS);
    }

    public static boolean isBalanced(String input, Map<Character, Character> pairs) {
        Deque<Character> openBrackets = new ArrayDeque<>();
        Set<Character> openingBrackets = new HashSet<>(pairs.values());

        for (char current : input.toCharArray()) {
            if (isOpeningBracket(current, openingBrackets)) {
                openBrackets.push(current);
            } else {
                Character expectedOpening = pairs.get(current);
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
