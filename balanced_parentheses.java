import java.util.ArrayDeque;
import java.util.Deque;

class BalancedParentheses {
    private static final String OPENING_BRACKETS = "({[";
    private static final String CLOSING_BRACKETS = ")}]";

    public static boolean isBalanced(String s) {
        Deque<Character> brackets = new ArrayDeque<>();

        for (char c : s.toCharArray()) {
            if (isOpeningBracket(c)) {
                brackets.push(c);
            } else if (isClosingBracket(c)) {
                if (brackets.isEmpty() || !isMatchingPair(brackets.pop(), c)) {
                    return false;
                }
            }
        }

        return brackets.isEmpty();
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
        String s = "[()()]{}";
        System.out.println(isBalanced(s));
    }
}
