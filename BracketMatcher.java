final class BracketMatcher {
    private final BracketProfile bracketProfile;
    private char[] closingStack;
    private int depth;

    BracketMatcher(BracketProfile bracketProfile) {
        this.bracketProfile = bracketProfile;
    }

    boolean matches(CharSequence input) {
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
        if (!bracketProfile.isBracket(c)) {
            return true;
        }

        if (bracketProfile.isOpeningBracket(c)) {
            closingStack[depth++] = bracketProfile.expectedClosingOf(c);
            return true;
        }

        return depth > 0 && closingStack[--depth] == c;
    }
}
