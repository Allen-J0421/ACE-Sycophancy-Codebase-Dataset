import java.util.Objects;

public final class BalancedParentheses {
    private static final String DEFAULT_SAMPLE = "[()()]{}";
    private static final BracketProfile DEFAULT_BRACKETS = BracketProfile.standard();

    private BalancedParentheses() {
    }

    public static boolean isBalanced(CharSequence input) {
        return isBalanced(input, DEFAULT_BRACKETS);
    }

    public static boolean isBalanced(CharSequence input, BracketProfile bracketProfile) {
        Objects.requireNonNull(input, "input");
        Objects.requireNonNull(bracketProfile, "bracketProfile");
        return new BracketMatcher(bracketProfile).matches(input);
    }

    public static void main(String[] args) {
        String sample = args.length > 0 ? args[0] : DEFAULT_SAMPLE;
        System.out.println(isBalanced(sample));
    }
}
