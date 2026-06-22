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
        return new BalanceChecker(bracketProfile).check(input);
    }

    public static void main(String[] args) {
        String sample = args.length > 0 ? args[0] : DEFAULT_SAMPLE;
        System.out.println(isBalanced(sample));
    }

    private static final class BalanceChecker {
        private final BracketProfile bracketProfile;
        private char[] closingStack;
        private int depth;

        private BalanceChecker(BracketProfile bracketProfile) {
            this.bracketProfile = bracketProfile;
        }

        private boolean check(CharSequence input) {
            closingStack = new char[input.length()];
            depth = 0;
            for (int i = 0, length = input.length(); i < length; i++) {
                if (!accept(input.charAt(i))) {
                    return false;
                }
            }
            return depth == 0;
        }

        private boolean accept(char c) {
            if (!bracketProfile.containsBracket(c)) {
                return true;
            }

            if (bracketProfile.isOpeningBracket(c)) {
                closingStack[depth++] = bracketProfile.expectedClosingOf(c);
                return true;
            }

            return depth > 0 && closingStack[--depth] == c;
        }
    }
}
