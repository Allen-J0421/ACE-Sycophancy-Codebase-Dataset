import java.util.ArrayDeque;
import java.util.Deque;

public class BalancedParentheses {
    private BalancedParentheses() {}

    public static boolean isBalanced(String s) {
        Deque<Character> brackets = new ArrayDeque<>();

        for (char c : s.toCharArray()) {
            if (isOpeningBracket(c)) {
                brackets.push(c);
                continue;
            }

            if (isClosingBracket(c)) {
                if (brackets.isEmpty() || !isMatchingPair(brackets.pop(), c)) {
                    return false;
                }
            }
        }

        return brackets.isEmpty();
    }

    private static boolean isOpeningBracket(char c) {
        return c == '(' || c == '{' || c == '[';
    }

    private static boolean isClosingBracket(char c) {
        return c == ')' || c == '}' || c == ']';
    }

    private static boolean isMatchingPair(char opening, char closing) {
        return (opening == '(' && closing == ')')
            || (opening == '{' && closing == '}')
            || (opening == '[' && closing == ']');
    }

    public static void main(String[] args) {
        String s = "[()()]{}";
        System.out.println((isBalanced(s) ? "true" : "false"));
    }
}
