import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Set;

public class BalancedParentheses {
    // Single source of truth: closing bracket -> matching opening bracket.
    private static final Map<Character, Character> PAIRS = Map.of(
        ')', '(',
        '}', '{',
        ']', '['
    );
    private static final Set<Character> OPENERS = Set.copyOf(PAIRS.values());

    public static boolean isBalanced(String s) {
        Deque<Character> stack = new ArrayDeque<>();
        for (char c : s.toCharArray()) {
            if (OPENERS.contains(c)) {
                stack.push(c);
            } else if (PAIRS.containsKey(c)) {
                if (stack.isEmpty() || stack.pop() != PAIRS.get(c)) {
                    return false;
                }
            }
            // Any other character is ignored.
        }
        return stack.isEmpty();
    }

    public static void main(String[] args) {
        String s = "[()()]{}";
        System.out.println(isBalanced(s));
    }
}
