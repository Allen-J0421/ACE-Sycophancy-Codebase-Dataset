import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

public class BalancedParentheses {
    private static final Map<Character, Character> PAIRS = Map.of(
        ')', '(',
        '}', '{',
        ']', '['
    );

    public static boolean isBalanced(String s) {
        Deque<Character> stack = new ArrayDeque<>();
        for (char c : s.toCharArray()) {
            Character open = PAIRS.get(c);
            if (open == null) {
                // Opening bracket (or any other character we track as opening).
                if (c == '(' || c == '{' || c == '[') {
                    stack.push(c);
                }
            } else if (stack.isEmpty() || stack.pop() != open) {
                // Closing bracket with no match.
                return false;
            }
        }
        return stack.isEmpty();
    }

    public static void main(String[] args) {
        String s = "[()()]{}";
        System.out.println(isBalanced(s));
    }
}
