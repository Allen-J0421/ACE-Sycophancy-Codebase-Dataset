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
        BracketToken token = bracketProfile.tokenFor(c);
        if (token == null) {
            return true;
        }

        if (token.isOpening()) {
            closingStack[depth++] = token.expectedClosing();
            return true;
        }

        return depth > 0 && closingStack[--depth] == token.character();
    }
}
