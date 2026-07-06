import java.util.Map;
import java.util.stream.IntStream;

public class BalancedParentheses {

    private static final Map<Character, Character> DEFAULT_PAIRS = Map.of(
        ')', '(',
        '}', '{',
        ']', '['
    );

    public static boolean isBalanced(String s) {
        return validate(s).balanced();
    }

    public static boolean isBalanced(String s, Map<Character, Character> pairs) {
        return validate(s, pairs).balanced();
    }

    public static BalancedParenthesesResult validate(String s) {
        return validate(s, DEFAULT_PAIRS);
    }

    public static BalancedParenthesesResult validate(String s, Map<Character, Character> pairs) {
        ValidationContext ctx = new ValidationContext(new BracketMatcher(pairs));
        IntStream.range(0, s.length())
            .allMatch(i -> ctx.process(s.charAt(i), i));
        return ctx.result();
    }

    public static void main(String[] args) {
        String s = "[()()]{}";
        BalancedParenthesesResult result = validate(s);
        System.out.println(result.balanced());
    }
}
