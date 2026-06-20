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
