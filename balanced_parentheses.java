import java.util.ArrayDeque;
import java.util.Deque;

final class BalancedParentheses {
    private static final String OPENING_BRACKETS = "({[";
    private static final String CLOSING_BRACKETS = ")}]";

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
        return OPENING_BRACKETS.indexOf(c) != -1;
    }

    private static boolean isClosingBracket(char c) {
        return CLOSING_BRACKETS.indexOf(c) != -1;
    }

    private static boolean isMatchingPair(char opening, char closing) {
        int openingIndex = OPENING_BRACKETS.indexOf(opening);
        return openingIndex != -1 && CLOSING_BRACKETS.charAt(openingIndex) == closing;
    }

    public static void main(String[] args) {
        String sample = "[()()]{}";
        System.out.println(isBalanced(sample));
    }
}
