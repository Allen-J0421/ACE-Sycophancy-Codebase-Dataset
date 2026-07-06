import java.util.Map;

public class BalancedParentheses {

    private static final BracketValidator DEFAULT_VALIDATOR = new BracketValidator(
        new BracketConfig(
            new char[]{')', '}', ']'},
            new char[]{'(', '{', '['}
        )
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
        DEFAULT_VALIDATOR.validate(s);
    }

    public static void validate(String s, Map<Character, Character> pairs) {
        new BracketValidator(BracketConfig.from(pairs)).validate(s);
    }

    public static void main(String[] args) {
        String s = "[()()]{}";
        System.out.println(isBalanced(s));
    }
}
