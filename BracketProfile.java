import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class BracketProfile {
    private final Map<Character, BracketToken> bracketTokens;

    private BracketProfile(Map<Character, BracketToken> bracketTokens) {
        this.bracketTokens = bracketTokens;
    }

    public static BracketProfile standard() {
        return of(Map.of(
            '(', ')',
            '{', '}',
            '[', ']'
        ));
    }

    public static BracketProfile of(Map<Character, Character> openingToClosing) {
        Objects.requireNonNull(openingToClosing, "openingToClosing");
        Map<Character, Character> normalizedPairs = Map.copyOf(openingToClosing);
        Set<Character> closingBrackets = Set.copyOf(normalizedPairs.values());
        validatePairs(normalizedPairs, closingBrackets);
        return new BracketProfile(buildBracketTokens(normalizedPairs));
    }

    BracketToken tokenFor(char candidate) {
        return bracketTokens.get(candidate);
    }

    private static void validatePairs(
        Map<Character, Character> openingToClosing,
        Set<Character> closingBrackets
    ) {
        if (closingBrackets.size() != openingToClosing.size()) {
            throw new IllegalArgumentException("closing brackets must be unique");
        }

        Set<Character> overlappingBrackets = new HashSet<>(openingToClosing.keySet());
        overlappingBrackets.retainAll(closingBrackets);
        if (!overlappingBrackets.isEmpty()) {
            throw new IllegalArgumentException("opening and closing brackets must be distinct");
        }
    }

    private static Map<Character, BracketToken> buildBracketTokens(Map<Character, Character> openingToClosing) {
        Map<Character, BracketToken> bracketTokens = new HashMap<>();
        for (Map.Entry<Character, Character> entry : openingToClosing.entrySet()) {
            char opening = entry.getKey();
            char closing = entry.getValue();
            bracketTokens.put(opening, BracketToken.opening(opening, closing));
            bracketTokens.put(closing, BracketToken.closing(closing));
        }

        return Map.copyOf(bracketTokens);
    }
}
