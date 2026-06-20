import java.util.Map;
import java.util.Stack;

public class BalancedParentheses {
    private static final Map<Character, Character> MATCHING = Map.of(
        ')', '(',
        '}', '{',
        ']', '['
    );

    public static boolean isBalanced(String s) {
        Stack<Character> stack = new Stack<>();
        for (char c : s.toCharArray()) {
            if (c == '(' || c == '{' || c == '[') {
                stack.push(c);
            } else if (MATCHING.containsKey(c)) {
                if (stack.isEmpty() || stack.pop() != MATCHING.get(c)) {
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }

    public static void main(String[] args) {
        String s = "[()()]{}";
        System.out.println(isBalanced(s));
    }
}
