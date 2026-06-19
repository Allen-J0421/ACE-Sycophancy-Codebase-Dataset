/**
 * Strategy interface for character comparison in LCS computation.
 * Enables pluggable matching logic (case-sensitive, case-insensitive, custom rules).
 */
interface CharacterMatcher {
    /**
     * Determines if two characters match according to the matching strategy.
     *
     * @param a first character
     * @param b second character
     * @return true if characters match, false otherwise
     */
    boolean matches(char a, char b);
}

/**
 * Standard case-sensitive character matching.
 */
class StandardCharacterMatcher implements CharacterMatcher {
    @Override
    public boolean matches(char a, char b) {
        return a == b;
    }
}

/**
 * Case-insensitive character matching.
 * Treats uppercase and lowercase versions of the same letter as matching.
 */
class CaseInsensitiveCharacterMatcher implements CharacterMatcher {
    @Override
    public boolean matches(char a, char b) {
        return Character.toLowerCase(a) == Character.toLowerCase(b);
    }
}

/**
 * Whitespace-insensitive matching.
 * Ignores differences in whitespace characters.
 */
class WhitespaceInsensitiveMatcher implements CharacterMatcher {
    @Override
    public boolean matches(char a, char b) {
        boolean aIsSpace = Character.isWhitespace(a);
        boolean bIsSpace = Character.isWhitespace(b);

        if (aIsSpace && bIsSpace) {
            return true;
        }
        if (aIsSpace || bIsSpace) {
            return false;
        }
        return a == b;
    }
}
