import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

public class BalancedParentheses {
    private static final Map<Character, Character> MATCHING = Map.of(
        ')', '(',
        '}', '{',
        ']', '['
    );

    public static boolean isBalanced(String input) {
        Deque<Character> stack = new ArrayDeque<>();
        for (char c : input.toCharArray()) {
            if (c == '(' || c == '{' || c == '[') {
                stack.push(c);
            } else if (MATCHING.containsKey(c)) {
                char expected = MATCHING.get(c);
                if (stack.isEmpty() || stack.pop() != expected) {
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }

    public static void main(String[] args) {
        String expression = "[()()]{}";
        System.out.println(isBalanced(expression));
    }
}
