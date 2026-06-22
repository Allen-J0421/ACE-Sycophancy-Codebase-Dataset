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
        return of(Map.of('(', ')', '{', '}', '[', ']'));
    }

    public static BracketProfile of(Map<Character, Character> openingToClosing) {
        Objects.requireNonNull(openingToClosing, "openingToClosing");

        Map<Character, Character> pairs = Map.copyOf(openingToClosing);
        Set<Character> closingBrackets = Set.copyOf(pairs.values());
        validatePairs(pairs, closingBrackets);
        return new BracketProfile(buildBracketTokens(pairs));
    }

    BracketToken tokenFor(char c) {
        return bracketTokens.get(Character.valueOf(c));
    }

    private static void validatePairs(
        Map<Character, Character> openingToClosing,
        Set<Character> closingBrackets
    ) {
        if (closingBrackets.size() != openingToClosing.size()) {
            throw new IllegalArgumentException("closing brackets must be unique");
        }

        Set<Character> overlap = new HashSet<>(openingToClosing.keySet());
        overlap.retainAll(closingBrackets);
        if (!overlap.isEmpty()) {
            throw new IllegalArgumentException("opening and closing brackets must be distinct");
        }
    }

    private static Map<Character, BracketToken> buildBracketTokens(
        Map<Character, Character> openingToClosing
    ) {
        Map<Character, BracketToken> tokens = new HashMap<>();
        for (Map.Entry<Character, Character> entry : openingToClosing.entrySet()) {
            char opening = entry.getKey().charValue();
            char closing = entry.getValue().charValue();
            tokens.put(Character.valueOf(opening), BracketToken.opening(opening, closing));
            tokens.put(Character.valueOf(closing), BracketToken.closing(closing));
        }
        return Map.copyOf(tokens);
    }
}
