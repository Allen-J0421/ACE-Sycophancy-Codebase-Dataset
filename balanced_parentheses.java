import java.util.Map;
import java.util.stream.IntStream;

public class BalancedParentheses {

    private static final BracketConfig DEFAULT_CONFIG = new BracketConfig(
        new char[]{')', '}', ']'},
        new char[]{'(', '{', '['}
    );

    public static boolean isBalanced(String s) {
        try {
            validate(s);
            return true;
        } catch (InvalidBracketException e) {
            return false;
        }
    }

    public static boolean isBalanced(String s, Map<Character, Character> pairs) {
        try {
            validate(s, pairs);
            return true;
        } catch (InvalidBracketException e) {
            return false;
        }
    }

    public static void validate(String s) {
        validate(s, DEFAULT_CONFIG);
    }

    public static void validate(String s, Map<Character, Character> pairs) {
        validate(s, BracketConfig.from(pairs));
    }

    private static void validate(String s, BracketConfig config) {
        ValidationContext ctx = new ValidationContext(new BracketMatcher(config));
        IntStream.range(0, s.length())
            .forEach(i -> ctx.process(s.charAt(i), i));
        ctx.checkComplete();
    }

    public static void main(String[] args) {
        String s = "[()()]{}";
        System.out.println(isBalanced(s));
    }
}
