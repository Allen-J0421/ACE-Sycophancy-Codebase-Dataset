import java.util.Map;

public class BalancedParentheses {

    private static final Map<Character, Character> DEFAULT_PAIRS = Map.of(
        ')', '(',
        '}', '{',
        ']', '['
    );

    public static boolean isBalanced(String s) {
        return isBalanced(s, DEFAULT_PAIRS);
    }

    public static boolean isBalanced(String s, Map<Character, Character> pairs) {
        ValidationContext ctx = new ValidationContext(new BracketMatcher(pairs));
        return s.chars()
            .mapToObj(c -> (char) c)
            .allMatch(ctx::process)
            && ctx.isComplete();
    }

    public static void main(String[] args) {
        String s = "[()()]{}";
        System.out.println((isBalanced(s) ? "true" : "false"));
    }
}
